package com.example.movie_app.api

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?
)

data class MovieResponse(
    @SerializedName("results")
    val results: List<Movie>
)
