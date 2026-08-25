package com.example.movie_app.navigation

/**
 * Type-safe route definitions for the entire nav graph.
 *
 * Instead of raw strings like "DetailScreen/42" scattered across the code,
 * every route is defined exactly once here. A typo becomes a compile error,
 * not a runtime crash.
 */
sealed class Screen(val route: String) {

    /** The home screen — shows the list of popular movies. */
    object Home : Screen("home")

    /** The detail screen — shows a single movie. Route contains {movieId} as a path argument. */
    object Detail : Screen("detail/{movieId}") {
        /** Builds the concrete route to navigate to, e.g. "detail/123" */
        fun createRoute(movieId: Int) = "detail/$movieId"
    }
}
