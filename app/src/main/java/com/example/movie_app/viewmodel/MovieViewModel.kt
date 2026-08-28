package com.example.movie_app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_app.BuildConfig
import com.example.movie_app.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    object Loading : UiState()
    object Success : UiState()
    data class Error(val message: String) : UiState()
}

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    val movies = repository.getMovies()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        Log.d("MovieViewModel", "ViewModel initialized: $this")
        refreshMovies()
    }

    fun refreshMovies() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                repository.refreshMovies(BuildConfig.TMDB_API_KEY)
                Log.d("MovieViewModel", "Refresh successful.")
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Refresh failed: ${e.message}", e)
                _uiState.value = UiState.Error(e.message ?: "Something went wrong. Please retry.")
            }
        }
    }
}
