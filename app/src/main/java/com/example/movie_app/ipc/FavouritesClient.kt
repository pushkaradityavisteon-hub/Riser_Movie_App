package com.example.movie_app.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.movie_app.IFavouritesCallback
import com.example.movie_app.IFavouritesService
import com.example.movie_app.favourites.FavouritesService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouritesClient @Inject constructor(
    @ApplicationContext private val context: Context
) : IFavouritesClient {

    private var service: IFavouritesService? = null

    private val _favouriteIds = MutableStateFlow<Set<Int>>(emptySet())
    override val favouriteIds: StateFlow<Set<Int>> = _favouriteIds.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val callback = object : IFavouritesCallback.Stub() {
        override fun onMovieAdded(movieId: Int) {
            Log.d("FavouritesClient", "onMovieAdded: $movieId — Binder thread")
            _favouriteIds.value = _favouriteIds.value + movieId
        }

        override fun onMovieRemoved(movieId: Int) {
            Log.d("FavouritesClient", "onMovieRemoved: $movieId — Binder thread")
            _favouriteIds.value = _favouriteIds.value - movieId
        }
    }

    private var deathRecipient: IBinder.DeathRecipient? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            Log.d("FavouritesClient", "Service connected")
            service = IFavouritesService.Stub.asInterface(binder)
            service?.registerCallback(callback)
            val ids = service?.getFavouriteIds() ?: intArrayOf()
            _favouriteIds.value = ids.toSet()
            _isConnected.value = true

            deathRecipient = IBinder.DeathRecipient {
                Log.w("FavouritesClient", "FavouritesService died — reconnecting")
                _isConnected.value = false
                service = null
                bind()
            }
            binder.linkToDeath(deathRecipient!!, 0)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.w("FavouritesClient", "Service disconnected unexpectedly")
            service = null
            _isConnected.value = false
        }
    }

    override fun bind() {
        val intent = Intent(context, FavouritesService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        Log.d("FavouritesClient", "bindService called")
    }

    override fun unbind() {
        try {
            service?.unregisterCallback(callback)
            deathRecipient?.let { service?.asBinder()?.unlinkToDeath(it, 0) }
            deathRecipient = null
            context.unbindService(connection)
        } catch (e: Exception) {
            Log.e("FavouritesClient", "unbind error: ${e.message}")
        }
        service = null
        _isConnected.value = false
    }

    override fun addFavourite(movieId: Int, title: String, posterPath: String) {
        try {
            service?.addFavourite(movieId, title, posterPath)
        } catch (e: Exception) {
            Log.e("FavouritesClient", "addFavourite failed: ${e.message}")
        }
    }

    override fun removeFavourite(movieId: Int) {
        try {
            service?.removeFavourite(movieId)
        } catch (e: Exception) {
            Log.e("FavouritesClient", "removeFavourite failed: ${e.message}")
        }
    }

    override fun isFavourite(movieId: Int): Boolean {
        return try {
            service?.isFavourite(movieId) ?: false
        } catch (e: Exception) {
            Log.e("FavouritesClient", "isFavourite failed: ${e.message}")
            false
        }
    }
}
