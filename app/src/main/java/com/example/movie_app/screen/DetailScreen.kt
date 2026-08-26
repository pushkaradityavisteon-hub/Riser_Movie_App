package com.example.movie_app.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.movie_app.domain.MovieViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailScreen(movieId: Int, modifier: Modifier, viewModel: MovieViewModel) {
    Log.d("DetailScreen", "viewModel: ${System.identityHashCode(viewModel)}")

    val movies by viewModel.movies.collectAsState()
    val movie = movies.find { it.id == movieId }

    Column(

            modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        if (movie != null) {

            // ----------------------------------------------------------------
            // Poster image — full width at the top, shown when available
            // w500 = 500px wide version from TMDB image CDN
            // ----------------------------------------------------------------
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
                // Placeholder when no poster is available
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.LightGray)
                )
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = movie.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Release date — shown only when available
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

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color.LightGray
                )

                // Overview
                Text(
                    text = movie.overview,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.DarkGray
                )
            }

        } else {
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
