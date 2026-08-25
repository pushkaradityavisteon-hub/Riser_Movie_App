package com.example.movie_app.domain

import com.example.movie_app.api.MovieApi
import com.example.movie_app.data.MovieDao
import com.example.movie_app.data.MovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val dao: MovieDao,
    private val api: MovieApi
) {

    /** Reactive stream of cached movies. UI always reads from Room, never the network. */
    fun getMovies(): Flow<List<MovieEntity>> = dao.getMovies()

    /**
     * Fetches fresh data from the network and syncs it into Room.
     * Clears stale movies first so the local cache stays consistent.
     * Throws on failure — the ViewModel catches and sets the error state.
     */
    suspend fun refreshMovies(apiKey: String) {
        withContext(Dispatchers.IO) {
            val response = api.getPopularMovies(apiKey)
            if (response.results.isNotEmpty()) {
                val entities = response.results.map { movie ->
                    MovieEntity(
                        id = movie.id,
                        title = movie.title ?: "Unknown Title",
                        overview = movie.overview ?: "No overview available",
                        posterPath = movie.posterPath,
                        releaseDate = movie.releaseDate
                    )
                }
                dao.clearMovies()
                dao.insertMovies(entities)
            }
        }
    }
}
