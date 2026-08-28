package com.example.movie_app.screen

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.movie_app.service.MovieSyncService
import com.example.movie_app.viewmodel.MovieViewModel
import com.example.movie_app.viewmodel.UiState
import com.example.movie_app.navigation.Screen
import com.example.movie_app.data.preferences.PrefrenceManager

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen(navController: NavController, modifier: Modifier, viewModel: MovieViewModel) {
    Log.d("HomeScreen", "viewModel: ${System.identityHashCode(viewModel)}")

    val movies by viewModel.movies.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val activity = LocalContext.current as? Activity
    val context = LocalContext.current
    val preferenceManager = PrefrenceManager(context)
    val username= preferenceManager.getUserName()?:"Guest"

    // Pressing back on the home screen exits the app
    BackHandler {
        preferenceManager.logout()
        activity?.finish()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ) {
        if (movies.isNotEmpty()) {
            // ----------------------------------------------------------------
            // Success — movie list is populated, show it
            // ----------------------------------------------------------------
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Popular Movies for ${username}",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(movies) { movie ->
                        // ----------------------------------------------------
                        // Each movie row: thumbnail on the left, title on right
                        // w185 = small size from TMDB CDN (saves bandwidth)
                        // ----------------------------------------------------
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .fillMaxWidth()
                                .background(Color.LightGray)
                                .clickable {
                                    navController.navigate(Screen.Detail.createRoute(movie.id))
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Poster thumbnail
                            if (!movie.posterPath.isNullOrBlank()) {
                                GlideImage(
                                    model = "https://image.tmdb.org/t/p/w185${movie.posterPath}",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(100.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // Grey placeholder when no poster available
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(100.dp)
                                        .background(Color.Gray)
                                )
                            }

                            // Movie title
                            Text(
                                text = movie.title,
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // ----------------------------------------------------------------
            // Empty list — show loading spinner, error message, or nothing
            // ----------------------------------------------------------------
            when (val state = uiState) {
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Text(
                            text = "Fetching popular movies…",
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is UiState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Failed to load movies",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = state.message,
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(onClick = {
                            val intent = Intent(context, MovieSyncService::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(intent)
                            } else {
                                context.startService(intent)
                            }
                        }) {
                            Text(text = "Retry")
                        }
                    }
                }

                is UiState.Success -> {
                    Text(
                        text = "No movies found.",
                        color = Color.White,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                }
            }
        }
    }
}
