package com.example.movie_app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.movie_app.navigation.Screen
import com.example.movie_app.viewmodel.FavouritesViewModel
import com.example.movie_app.viewmodel.MovieViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FavouritesScreen(
    navController: NavController,
    movieViewModel: MovieViewModel,
    favouritesViewModel: FavouritesViewModel
) {
    val allMovies by movieViewModel.movies.collectAsState()
    val favouriteIds by favouritesViewModel.favouriteIds.collectAsState()
    val isConnected by favouritesViewModel.isConnected.collectAsState()

    val favouriteMovies = allMovies.filter { it.id in favouriteIds }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ) {
        Text(
            text = "My Favourites",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
        )

        if (!isConnected) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Connecting to Favourites service...",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
            return@Column
        }

        if (favouriteMovies.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No favourites yet",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Tap the heart on any movie to save it here",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(favouriteMovies, key = { it.id }) { movie ->
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
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(100.dp)
                                    .background(Color.Gray)
                            )
                        }

                        Text(
                            text = movie.title,
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )

                        IconButton(
                            onClick = {
                                favouritesViewModel.toggleFavourite(
                                    movie.id,
                                    movie.title,
                                    movie.posterPath ?: ""
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Remove from favourites",
                                tint = Color.Red
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
}
