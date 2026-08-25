package com.example.movie_app.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movie_app.api.MovieViewModel


@Composable
fun DetailScreen(movieId: Int, modifier: Modifier, viewModel: MovieViewModel) {

    Log.d("DetailScreen", "viewModel: ${System.identityHashCode(viewModel)}")
    val movies by viewModel.movies.collectAsState()
    val movie = movies.find { it.id == movieId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        if (movie != null) {
            Text(
                text = movie.title,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 20.dp))
            Text(
                text = movie.overview,
                fontSize = 20.sp,
                modifier = Modifier.padding(20.dp)
            )
        } else {
            Text(
                text = "Movie Not Found",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth()
            )
        }
    }
}