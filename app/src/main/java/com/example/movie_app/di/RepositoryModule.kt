package com.example.movie_app.di

import com.example.movie_app.data.repository.MovieRepository
import com.example.movie_app.domain.repository.IMovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        movieRepository: MovieRepository
    ): IMovieRepository
}
