package com.example.movie_app.di

import com.example.movie_app.ipc.DownloadClient
import com.example.movie_app.ipc.FavouritesClient
import com.example.movie_app.ipc.IDownloadClient
import com.example.movie_app.ipc.IFavouritesClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IpcModule {

    @Binds
    @Singleton
    abstract fun bindFavouritesClient(
        favouritesClient: FavouritesClient
    ): IFavouritesClient

    @Binds
    @Singleton
    abstract fun bindDownloadClient(
        downloadClient: DownloadClient
    ): IDownloadClient
}
