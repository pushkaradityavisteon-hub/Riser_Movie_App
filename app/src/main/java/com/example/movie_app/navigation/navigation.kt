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
import com.example.movie_app.viewmodel.MovieViewModel
import com.example.movie_app.screen.DetailScreen
import com.example.movie_app.screen.HomeScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel: MovieViewModel = hiltViewModel()
    val context=LocalContext.current
    val prefrenceManager= PrefrenceManager(context)
    val islogged = prefrenceManager.getLoginStatus()


    NavHost(
        navController = navController,
        startDestination = if(islogged) Screen.Home.route else Screen.Login.route,
        modifier = modifier,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                navController = navController,
                modifier = Modifier,
                viewModel = viewModel
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
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
