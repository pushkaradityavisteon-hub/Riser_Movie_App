package com.example.movie_app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.movie_app.BuildConfig
import com.example.movie_app.domain.repository.IMovieRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MovieSyncService : Service() {

    @Inject
    lateinit var repository: IMovieRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "movie_sync_channel",
            "Movie Sync",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "movie_sync_channel")
            .setContentTitle("Movie App")
            .setContentText("Syncing movies...")
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .build()

        startForeground(1, notification)

        serviceScope.launch {
            try {
                repository.refreshMovies(BuildConfig.TMDB_API_KEY)
            } catch (e: Exception) {
                Log.e("MovieSyncService", "Sync failed: ${e.message}")
            } finally {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
