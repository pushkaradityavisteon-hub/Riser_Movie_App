package com.example.movie_app.ipc

import kotlinx.coroutines.flow.StateFlow

interface IDownloadClient {
    val downloadStates: StateFlow<Map<Int, DownloadState>>
    val isConnected: StateFlow<Boolean>

    fun bind()
    fun unbind()
    fun downloadPoster(movieId: Int, posterUrl: String, title: String)
    fun cancelDownload(movieId: Int)
    fun isDownloaded(movieId: Int): Boolean
    fun getDownloadState(movieId: Int): DownloadState
}
