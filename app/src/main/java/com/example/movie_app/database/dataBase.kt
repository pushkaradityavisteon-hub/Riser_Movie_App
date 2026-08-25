package com.example.movie_app.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movie_app.database.MovieDao
import com.example.movie_app.database.MovieEntity

@Database(entities = [MovieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}