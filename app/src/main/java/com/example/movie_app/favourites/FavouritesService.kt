package com.example.movie_app.favourites

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.os.RemoteCallbackList
import android.util.Log
import com.example.movie_app.IFavouritesCallback
import com.example.movie_app.IFavouritesService

class FavouritesService : Service() {

    private val callbacks = RemoteCallbackList<IFavouritesCallback>()

    private val favouriteIdsSet = mutableSetOf<Int>()
    private val favouriteDataMap = mutableMapOf<Int, Pair<String, String>>()

    // SharedPreferences key — persists favourite IDs across process death and app restarts
    private lateinit var prefs: SharedPreferences
    private val PREFS_NAME = "favourites_prefs"
    private val KEY_IDS = "favourite_ids"

    private fun saveToPrefs() {
        val stringSet = favouriteIdsSet.map { it.toString() }.toSet()
        prefs.edit().putStringSet(KEY_IDS, stringSet).apply()
    }

    private fun loadFromPrefs() {
        val stringSet = prefs.getStringSet(KEY_IDS, emptySet()) ?: emptySet()
        favouriteIdsSet.clear()
        stringSet.mapNotNullTo(favouriteIdsSet) { it.toIntOrNull() }
        Log.d("FavouritesService", "Loaded ${favouriteIdsSet.size} favourites from prefs")
    }

    private val binder = object : IFavouritesService.Stub() {

        override fun addFavourite(movieId: Int, title: String, posterPath: String) {
            synchronized(favouriteIdsSet) {
                favouriteIdsSet.add(movieId)
                favouriteDataMap[movieId] = Pair(title, posterPath)
                saveToPrefs()  // persist immediately
            }
            Log.d("FavouritesService", "Added favourite: $movieId - $title")
            notifyAdded(movieId)
        }

        override fun removeFavourite(movieId: Int) {
            synchronized(favouriteIdsSet) {
                favouriteIdsSet.remove(movieId)
                favouriteDataMap.remove(movieId)
                saveToPrefs()  // persist immediately
            }
            Log.d("FavouritesService", "Removed favourite: $movieId")
            notifyRemoved(movieId)
        }

        override fun isFavourite(movieId: Int): Boolean {
            return synchronized(favouriteIdsSet) { favouriteIdsSet.contains(movieId) }
        }

        override fun getFavouriteIds(): IntArray {
            return synchronized(favouriteIdsSet) { favouriteIdsSet.toIntArray() }
        }

        override fun registerCallback(callback: IFavouritesCallback) {
            callbacks.register(callback)
            Log.d("FavouritesService", "Callback registered")
        }

        override fun unregisterCallback(callback: IFavouritesCallback) {
            callbacks.unregister(callback)
            Log.d("FavouritesService", "Callback unregistered")
        }
    }

    private fun notifyAdded(movieId: Int) {
        val count = callbacks.beginBroadcast()
        for (i in 0 until count) {
            callbacks.getBroadcastItem(i).onMovieAdded(movieId)
        }
        callbacks.finishBroadcast()
    }

    private fun notifyRemoved(movieId: Int) {
        val count = callbacks.beginBroadcast()
        for (i in 0 until count) {
            callbacks.getBroadcastItem(i).onMovieRemoved(movieId)
        }
        callbacks.finishBroadcast()
    }

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        loadFromPrefs()  // restore favourites saved from previous session
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        callbacks.kill()
        Log.d("FavouritesService", "Service destroyed")
    }
}
