package com.example.movie_app.navigation

import LoginScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movie_app.data.preferences.PrefrenceManager
import com.example.movie_app.screen.DetailScreen
import com.example.movie_app.screen.FavouritesScreen
import com.example.movie_app.screen.HomeScreen
import com.example.movie_app.viewmodel.DownloadViewModel
import com.example.movie_app.viewmodel.FavouritesViewModel
import com.example.movie_app.viewmodel.MovieViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefrenceManager = PrefrenceManager(context)
    val isLogged = prefrenceManager.getLoginStatus()

    val movieViewModel: MovieViewModel = hiltViewModel()
    val favouritesViewModel: FavouritesViewModel = hiltViewModel()
    val downloadViewModel: DownloadViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = if (isLogged) Screen.Home.route else Screen.Login.route,
        modifier = modifier,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                modifier = Modifier,
                viewModel = movieViewModel,
                favouritesViewModel = favouritesViewModel
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Favourites.route) {
            FavouritesScreen(
                navController = navController,
                movieViewModel = movieViewModel,
                favouritesViewModel = favouritesViewModel
            )
        }

        composable(Screen.Detail.route) { backStackEntry ->
            val movieId = backStackEntry.arguments
                ?.getString("movieId")
                ?.toIntOrNull() ?: 0
            DetailScreen(
                movieId = movieId,
                modifier = Modifier,
                viewModel = movieViewModel,
                favouritesViewModel = favouritesViewModel,
                downloadViewModel = downloadViewModel,
                navController = navController
            )
        }
    }
}
