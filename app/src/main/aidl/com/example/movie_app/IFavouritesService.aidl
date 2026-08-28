package com.example.movie_app;

import com.example.movie_app.IFavouritesCallback;

interface IFavouritesService {
    void addFavourite(int movieId, String title, String posterPath);
    void removeFavourite(int movieId);
    boolean isFavourite(int movieId);
    int[] getFavouriteIds();
    void registerCallback(IFavouritesCallback callback);
    void unregisterCallback(IFavouritesCallback callback);
}
