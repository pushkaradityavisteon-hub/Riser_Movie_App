package com.example.movie_app.data.repository

import com.example.movie_app.data.local.MovieDao
import com.example.movie_app.data.local.MovieEntity
import com.example.movie_app.data.remote.MovieApi
import com.example.movie_app.domain.model.Movie
import com.example.movie_app.domain.repository.IMovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val dao: MovieDao,
    private val api: MovieApi
) : IMovieRepository {

    override fun getMovies(): Flow<List<Movie>> {
        return dao.getMovies().map { entities ->
            entities.map { entity ->
                Movie(
                    id = entity.id,
                    title = entity.title,
                    overview = entity.overview,
                    posterPath = entity.posterPath,
                    releaseDate = entity.releaseDate
                )
            }
        }
    }

    override suspend fun refreshMovies(apiKey: String) {
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
