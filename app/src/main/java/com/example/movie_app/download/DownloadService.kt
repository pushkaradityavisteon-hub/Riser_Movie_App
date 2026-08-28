package com.example.movie_app.download

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
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
import java.io.File
import java.io.FileOutputStream

class DownloadService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val callbacks = RemoteCallbackList<IDownloadCallback>()
    private val downloadedPaths = mutableMapOf<Int, String>()
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
                    val outputFile = File(filesDir, "poster_$movieId.jpg")

                    FileOutputStream(outputFile).use { out ->
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

                    synchronized(downloadedPaths) {
                        downloadedPaths[movieId] = outputFile.absolutePath
                    }
                    notifyProgress(movieId, 100)
                    notifyComplete(movieId, outputFile.absolutePath)
                    Log.d("DownloadService", "Download complete for $movieId: ${outputFile.absolutePath}")

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
            return synchronized(downloadedPaths) {
                downloadedPaths.containsKey(movieId) &&
                        File(downloadedPaths[movieId]!!).exists()
            }
        }

        override fun getLocalPath(movieId: Int): String? {
            return synchronized(downloadedPaths) { downloadedPaths[movieId] }
        }

        override fun registerCallback(callback: IDownloadCallback) {
            callbacks.register(callback)
            Log.d("DownloadService", "Callback registered")
        }

        override fun unregisterCallback(callback: IDownloadCallback) {
            callbacks.unregister(callback)
            Log.d("DownloadService", "Callback unregistered")
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
        Log.d("DownloadService", "Service destroyed")
    }
}
