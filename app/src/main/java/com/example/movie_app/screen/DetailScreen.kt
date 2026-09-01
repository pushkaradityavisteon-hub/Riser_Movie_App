package com.example.movie_app.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import com.example.movie_app.screen.components.BackButton
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import com.example.movie_app.ipc.DownloadState
import com.example.movie_app.viewmodel.DownloadViewModel
import com.example.movie_app.viewmodel.FavouritesViewModel
import com.example.movie_app.viewmodel.MovieViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailScreen(
    movieId: Int,
    modifier: Modifier,
    viewModel: MovieViewModel,
    favouritesViewModel: FavouritesViewModel,
    downloadViewModel: DownloadViewModel,
    navController: NavController
) {
    Log.d("DetailScreen", "viewModel: ${System.identityHashCode(viewModel)}")

    val movies by viewModel.movies.collectAsState()
    val movie = movies.find { it.id == movieId }
    val context = LocalContext.current

    val favouriteIds by favouritesViewModel.favouriteIds.collectAsState()
    val isFavourite = movie?.id in favouriteIds

    val downloadStates by downloadViewModel.downloadStates.collectAsState()
    val downloadState = downloadStates[movieId] ?: DownloadState.Idle

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        if (movie != null) {

            Box {
                if (!movie.posterPath.isNullOrBlank()) {
                    GlideImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = "${movie.title} poster",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.LightGray)
                    )
                }

                // Back button overlaid on top of poster
                BackButton(
                    navController = navController,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movie.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
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
                            imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites",
                            tint = if (isFavourite) Color.Red else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                if (!movie.releaseDate.isNullOrBlank()) {
                    Text(
                        text = "Released: ${movie.releaseDate}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.LightGray
                )

                Text(
                    text = movie.overview,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (downloadState) {
                    is DownloadState.Idle -> {
                        OutlinedButton(
                            onClick = {
                                downloadViewModel.downloadPoster(
                                    movie.id,
                                    movie.posterPath ?: "",
                                    movie.title
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Download Poster")
                        }
                    }

                    is DownloadState.Downloading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Downloading poster...",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                                Text(
                                    text = "${downloadState.progress}%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                            }
                            LinearProgressIndicator(
                                progress = downloadState.progress / 100f,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedButton(
                                onClick = { downloadViewModel.cancelDownload(movie.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Cancel")
                            }
                        }
                    }

                    is DownloadState.Done -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F5E9))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Poster saved offline",
                                color = Color(0xFF388E3C),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    is DownloadState.Failed -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Download failed: ${downloadState.reason}",
                                color = Color.Red,
                                fontSize = 13.sp
                            )
                            OutlinedButton(
                                onClick = {
                                    downloadViewModel.downloadPoster(
                                        movie.id,
                                        movie.posterPath ?: "",
                                        movie.title
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Retry Download")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.com/search?q=${movie.title}")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Search on Google")
                }
            }

        } else {
            BackButton(
                navController = navController,
                tint = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = "Movie not found.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )
        }
    }
}
