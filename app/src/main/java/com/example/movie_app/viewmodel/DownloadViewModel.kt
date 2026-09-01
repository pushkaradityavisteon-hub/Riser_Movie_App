package com.example.movie_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_app.ipc.IDownloadClient
import com.example.movie_app.ipc.DownloadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val client: IDownloadClient
) : ViewModel() {

    val downloadStates: StateFlow<Map<Int, DownloadState>> = client.downloadStates
    val isConnected: StateFlow<Boolean> = client.isConnected

    fun downloadPoster(movieId: Int, posterUrl: String, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            client.downloadPoster(movieId, posterUrl, title)
        }
    }

    fun cancelDownload(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            client.cancelDownload(movieId)
        }
    }

    fun getStateForMovie(movieId: Int): DownloadState {
        return downloadStates.value[movieId] ?: DownloadState.Idle
    }

    fun isDownloaded(movieId: Int): Boolean {
        return downloadStates.value[movieId] is DownloadState.Done
    }
}
