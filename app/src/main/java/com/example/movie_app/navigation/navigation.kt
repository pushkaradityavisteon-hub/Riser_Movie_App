package com.example.movie_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movie_app.api.MovieViewModel
import com.example.movie_app.screen.HomeScreen
import com.example.movie_app.screen.DetailScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier)
{
    val navController = rememberNavController()
    val viewModel: MovieViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "HomeScreen",
        modifier = modifier,
    )
    {
        composable("HomeScreen") {

            HomeScreen(navController, modifier = Modifier, viewModel = viewModel)
        }
        composable("DetailScreen/{movieId}") { backStackEntry ->
            val movieId = backStackEntry.arguments
                ?.getString("movieId")
                ?.toInt() ?: 0
            DetailScreen(movieId, modifier = Modifier, viewModel = viewModel)
        }
    }
}
