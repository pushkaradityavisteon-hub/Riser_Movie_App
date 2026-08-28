package com.example.movie_app.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.movie_app.IDownloadCallback
import com.example.movie_app.IDownloadService
import com.example.movie_app.download.DownloadService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

sealed class DownloadState {
    object Idle : DownloadState()
    data class Downloading(val progress: Int) : DownloadState()
    data class Done(val localPath: String) : DownloadState()
    data class Failed(val reason: String) : DownloadState()
}

@Singleton
class DownloadClient @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var service: IDownloadService? = null

    private val _downloadStates = MutableStateFlow<Map<Int, DownloadState>>(emptyMap())
    val downloadStates: StateFlow<Map<Int, DownloadState>> = _downloadStates.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val callback = object : IDownloadCallback.Stub() {
        override fun onProgress(movieId: Int, percent: Int) {
            Log.d("DownloadClient", "onProgress: $movieId = $percent% — Binder thread")
            _downloadStates.update { it + (movieId to DownloadState.Downloading(percent)) }
        }

        override fun onComplete(movieId: Int, localPath: String) {
            Log.d("DownloadClient", "onComplete: $movieId — Binder thread")
            _downloadStates.update { it + (movieId to DownloadState.Done(localPath)) }
        }

        override fun onFailed(movieId: Int, reason: String) {
            Log.e("DownloadClient", "onFailed: $movieId reason=$reason — Binder thread")
            _downloadStates.update { it + (movieId to DownloadState.Failed(reason)) }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Log.d("DownloadClient", "Service connected")
            service = IDownloadService.Stub.asInterface(binder)
            service?.registerCallback(callback)
            _isConnected.value = true

            binder.linkToDeath({
                Log.w("DownloadClient", "DownloadService died — reconnecting")
                _isConnected.value = false
                service = null
                bind()
            }, 0)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.w("DownloadClient", "Service disconnected unexpectedly")
            service = null
            _isConnected.value = false
        }
    }

    fun bind() {
        val intent = Intent(context, DownloadService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        Log.d("DownloadClient", "bindService called")
    }

    fun unbind() {
        try {
            service?.unregisterCallback(callback)
            context.unbindService(connection)
        } catch (e: Exception) {
            Log.e("DownloadClient", "unbind error: ${e.message}")
        }
        service = null
        _isConnected.value = false
    }

    fun downloadPoster(movieId: Int, posterUrl: String, title: String) {
        _downloadStates.update { it + (movieId to DownloadState.Downloading(0)) }
        try {
            service?.downloadPoster(movieId, posterUrl, title)
        } catch (e: Exception) {
            Log.e("DownloadClient", "downloadPoster IPC failed: ${e.message}")
            _downloadStates.update { it + (movieId to DownloadState.Failed(e.message ?: "IPC error")) }
        }
    }

    fun cancelDownload(movieId: Int) {
        try {
            service?.cancelDownload(movieId)
            _downloadStates.update { it + (movieId to DownloadState.Idle) }
        } catch (e: Exception) {
            Log.e("DownloadClient", "cancelDownload IPC failed: ${e.message}")
        }
    }

    fun isDownloaded(movieId: Int): Boolean {
        return try {
            service?.isDownloaded(movieId) ?: false
        } catch (e: Exception) {
            false
        }
    }

    fun getDownloadState(movieId: Int): DownloadState {
        return _downloadStates.value[movieId] ?: DownloadState.Idle
    }
}
