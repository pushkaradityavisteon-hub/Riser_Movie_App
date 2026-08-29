package com.example.movie_app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.movie_app.ipc.DownloadClient
import com.example.movie_app.ipc.FavouritesClient
import com.example.movie_app.navigation.AppNavigation
import com.example.movie_app.service.MovieSyncService
import com.example.movie_app.ui.theme.Movie_appTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var favouritesClient: FavouritesClient
    @Inject lateinit var downloadClient: DownloadClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LIFECYCLE", "ONCREATE")

        val syncIntent = Intent(this, MovieSyncService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(syncIntent)
        } else {
            startService(syncIntent)
        }

        // Bind IPC clients for the full activity lifetime so the connection
        // is not dropped when the user briefly switches to another app.
        favouritesClient.bind()
        downloadClient.bind()
        Log.d("LIFECYCLE", "IPC clients bound")

        enableEdgeToEdge()
        setContent {
            Movie_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("LIFECYCLE", "ONSTART")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LIFECYCLE", "ONSTOP")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LIFECYCLE", "ONRESUME")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LIFECYCLE", "ONPAUSE")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LIFECYCLE", "ONDESTROY")
        favouritesClient.unbind()
        downloadClient.unbind()
        Log.d("LIFECYCLE", "IPC clients unbound")
    }
}
