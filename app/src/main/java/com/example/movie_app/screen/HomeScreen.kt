package com.example.movie_app.screen

import android.R.attr.checked
import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.movie_app.api.MovieViewModel

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier, viewModel: MovieViewModel) {
    Log.d("HomeScreen", "viewModel: ${System.identityHashCode(viewModel)}")
    val movies by viewModel.movies.collectAsState()
    val activity = LocalContext.current as? Activity

    BackHandler {
        activity?.finish()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.DarkGray)
    ) {
        if (movies.isEmpty()) {
            Log.d("HomeScreen", "Movies list is empty")
            Text(
                text = "Fetching popular movies...\nPlease wait.",
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).padding(20.dp)
            )
        } else {
            Row(modifier=Modifier.fillMaxWidth())
            {
                Text(text="Popular Movies",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    color = Color.White,
                    modifier=Modifier.padding(start=100.dp,top=8.dp))
//                var checked by remember { mutableStateOf(false) }
//                Switch(
//                    modifier=Modifier.padding(start=45.dp),
//                    checked = checked,
//                    onCheckedChange = { newvalue->
//                        checked = newvalue
//                    },
//                    colors = SwitchDefaults.colors(
//                        checkedThumbColor = MaterialTheme.colorScheme.primary,
//                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
//                        uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
//                        uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
//                    )
//                )

            }
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top=50.dp)) {
                items(movies) { movie ->
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .clickable { navController.navigate("DetailScreen/${movie.id}") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = movie.title,
                            fontSize = 18.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

