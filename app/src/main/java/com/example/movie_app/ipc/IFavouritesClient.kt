package com.example.movie_app.ipc

import kotlinx.coroutines.flow.StateFlow

interface IFavouritesClient {
    val favouriteIds: StateFlow<Set<Int>>
    val isConnected: StateFlow<Boolean>

    fun bind()
    fun unbind()
    fun addFavourite(movieId: Int, title: String, posterPath: String)
    fun removeFavourite(movieId: Int)
    fun isFavourite(movieId: Int): Boolean
}
