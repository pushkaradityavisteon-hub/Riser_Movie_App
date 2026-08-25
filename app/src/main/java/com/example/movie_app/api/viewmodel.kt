package com.example.movie_app.api

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_app.database.DatabaseProvider
import com.example.movie_app.database.MovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = DatabaseProvider.getDatabase(application).movieDao()

    // Single source of truth: UI observes the Room database directly.
    val movies = dao.getMovies()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        Log.d("MovieViewModel", "ViewModel initialized. Fetching fresh data...$this")
        fetchPopularMovies()
    }

    private fun fetchPopularMovies() {
        viewModelScope.launch {
            try {
                val apiKey = "696b6d7d51ff4bdc1645a91a0bb19a93"
                
                // Fetch from network on IO thread
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.getPopularMovies(apiKey)
                }
                
                if (response.results.isNotEmpty()) {
                    Log.d("MovieViewModel", "API returned ${response.results.size} movies. Syncing to Room.")
                    saveMoviesToDb(response.results)
                }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Sync failed: ${e.message}", e)
            }
        }
    }

    private suspend fun saveMoviesToDb(apiMovies: List<Movie>) {
        withContext(Dispatchers.IO) {
            try {
                val entities = apiMovies.map {
                    MovieEntity(
                        id = it.id,
                        title = it.title ?: "Unknown Title",
                        overview = it.overview ?: "No overview available"
                    )
                }
                dao.insertMovies(entities)
                Log.d("MovieViewModel", "Successfully saved ${entities.size} movies to Room.")
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error saving to database", e)
            }
        }
    }
}
