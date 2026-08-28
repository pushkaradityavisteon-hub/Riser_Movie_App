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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.movie_app.navigation.AppNavigation
import com.example.movie_app.ui.theme.Movie_appTheme
import com.example.movie_app.screen.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LIFECYCLE","ONCREATE")
        val intent = Intent(this, com.example.movie_app.service.MovieSyncService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
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

    override fun onResume() {
        super.onResume()
        Log.d("LIFECYCLE","ONRESUME")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LIFECYCLE","ONPAUSE")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LIFECYCLE","ONSTOP")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LIFECYCLE","ONDESTROY")
    }

}



