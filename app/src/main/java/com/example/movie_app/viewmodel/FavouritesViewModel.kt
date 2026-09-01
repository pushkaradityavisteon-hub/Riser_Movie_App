package com.example.movie_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_app.ipc.IFavouritesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val client: IFavouritesClient
) : ViewModel() {

    val favouriteIds: StateFlow<Set<Int>> = client.favouriteIds
    val isConnected: StateFlow<Boolean> = client.isConnected

    fun toggleFavourite(movieId: Int, title: String, posterPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (client.isFavourite(movieId)) {
                client.removeFavourite(movieId)
            } else {
                client.addFavourite(movieId, title, posterPath)
            }
        }
    }

    fun isFavourite(movieId: Int): Boolean = favouriteIds.value.contains(movieId)
}
