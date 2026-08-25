package com.example.movie_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movie_app.domain.MovieViewModel
import com.example.movie_app.screen.DetailScreen
import com.example.movie_app.screen.HomeScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // Single ViewModel instance scoped to the entire nav graph.
    // Both HomeScreen and DetailScreen share this same instance.
    val viewModel: MovieViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                modifier = Modifier,
                viewModel = viewModel
            )
        }

        composable(Screen.Detail.route) { backStackEntry ->
            val movieId = backStackEntry.arguments
                ?.getString("movieId")
                ?.toIntOrNull() ?: 0
            DetailScreen(
                movieId = movieId,
                modifier = Modifier,
                viewModel = viewModel
            )
        }
    }
}
