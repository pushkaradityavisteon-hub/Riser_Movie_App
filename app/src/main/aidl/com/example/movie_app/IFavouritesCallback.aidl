package com.example.movie_app;

oneway interface IFavouritesCallback {
    void onMovieAdded(int movieId);
    void onMovieRemoved(int movieId);
}
