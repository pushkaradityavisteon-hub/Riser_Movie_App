package com.example.movie_app.database

import android.content.Context
import androidx.room.Room
import com.example.movie_app.db.AppDatabase

object DatabaseProvider {

    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "movie_db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}