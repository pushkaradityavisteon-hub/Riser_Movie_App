package com.example.movie_app.download

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.RemoteCallbackList
import android.provider.MediaStore
import android.util.Log
import com.example.movie_app.IDownloadCallback
import com.example.movie_app.IDownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class DownloadService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val callbacks = RemoteCallbackList<IDownloadCallback>()
    private val downloadedUris = mutableMapOf<Int, String>()
    private val activeDownloads = mutableSetOf<Int>()

    private val httpClient = OkHttpClient()

    private val binder = object : IDownloadService.Stub() {

        override fun downloadPoster(movieId: Int, posterUrl: String, title: String) {
            synchronized(activeDownloads) {
                if (activeDownloads.contains(movieId)) {
                    Log.d("DownloadService", "Download already in progress for $movieId")
                    return
                }
                activeDownloads.add(movieId)
            }

            serviceScope.launch {
                try {
                    Log.d("DownloadService", "Starting download for $movieId: $posterUrl")

                    val fullUrl = "https://image.tmdb.org/t/p/w500$posterUrl"
                    val request = Request.Builder().url(fullUrl).build()
                    val response = httpClient.newCall(request).execute()

                    if (!response.isSuccessful) {
                        notifyFailed(movieId, "HTTP ${response.code}")
                        return@launch
                    }

                    val body = response.body ?: run {
                        notifyFailed(movieId, "Empty response body")
                        return@launch
                    }

                    val totalBytes = body.contentLength()
                    val fileName = "poster_${title.replace(" ", "_")}_$movieId.jpg"

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MovieApp")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }

                    val resolver = contentResolver
                    val imageUri: Uri = resolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    ) ?: run {
                        notifyFailed(movieId, "MediaStore insert failed")
                        return@launch
                    }

                    resolver.openOutputStream(imageUri)?.use { out ->
                        body.byteStream().use { input ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            var totalRead = 0L

                            while (input.read(buffer).also { bytesRead = it } != -1) {
                                out.write(buffer, 0, bytesRead)
                                totalRead += bytesRead
                                if (totalBytes > 0) {
                                    val progress = ((totalRead * 100) / totalBytes).toInt()
                                    notifyProgress(movieId, progress)
                                }
                            }
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        resolver.update(imageUri, contentValues, null, null)
                    }

                    val savedPath = imageUri.toString()
                    synchronized(downloadedUris) {
                        downloadedUris[movieId] = savedPath
                    }

                    notifyProgress(movieId, 100)
                    notifyComplete(movieId, savedPath)
                    Log.d("DownloadService", "Saved to gallery: $savedPath")

                } catch (e: Exception) {
                    Log.e("DownloadService", "Download failed for $movieId: ${e.message}")
                    notifyFailed(movieId, e.message ?: "Unknown error")
                } finally {
                    synchronized(activeDownloads) { activeDownloads.remove(movieId) }
                }
            }
        }

        override fun cancelDownload(movieId: Int) {
            synchronized(activeDownloads) { activeDownloads.remove(movieId) }
            Log.d("DownloadService", "Cancelled download for $movieId")
        }

        override fun isDownloaded(movieId: Int): Boolean {
            return synchronized(downloadedUris) { downloadedUris.containsKey(movieId) }
        }

        override fun getLocalPath(movieId: Int): String? {
            return synchronized(downloadedUris) { downloadedUris[movieId] }
        }

        override fun registerCallback(callback: IDownloadCallback) {
            callbacks.register(callback)
        }

        override fun unregisterCallback(callback: IDownloadCallback) {
            callbacks.unregister(callback)
        }
    }

    private fun notifyProgress(movieId: Int, percent: Int) {
        val count = callbacks.beginBroadcast()
        for (i in 0 until count) {
            callbacks.getBroadcastItem(i).onProgress(movieId, percent)
        }
        callbacks.finishBroadcast()
    }

    private fun notifyComplete(movieId: Int, localPath: String) {
        val count = callbacks.beginBroadcast()
        for (i in 0 until count) {
            callbacks.getBroadcastItem(i).onComplete(movieId, localPath)
        }
        callbacks.finishBroadcast()
    }

    private fun notifyFailed(movieId: Int, reason: String) {
        val count = callbacks.beginBroadcast()
        for (i in 0 until count) {
            callbacks.getBroadcastItem(i).onFailed(movieId, reason)
        }
        callbacks.finishBroadcast()
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        callbacks.kill()
    }
}
