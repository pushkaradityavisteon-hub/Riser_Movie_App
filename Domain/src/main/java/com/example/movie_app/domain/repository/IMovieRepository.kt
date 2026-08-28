package com.example.movie_app.domain.repository

import com.example.movie_app.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface IMovieRepository {
    fun getMovies(): Flow<List<Movie>>
    suspend fun refreshMovies(apiKey: String)
}
