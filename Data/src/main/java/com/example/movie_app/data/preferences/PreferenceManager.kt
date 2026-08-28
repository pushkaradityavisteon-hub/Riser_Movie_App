package com.example.movie_app.data.preferences

import android.content.Context

class PrefrenceManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("Movie_app", Context.MODE_PRIVATE)

    fun saveLoginStatus(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
    }

    fun getLoginStatus(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun saveUserName(username: String) {
        sharedPreferences.edit().putString("username", username).apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun savePassword(password: String) {
        sharedPreferences.edit().putString("password", password).apply()
    }

    fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}
