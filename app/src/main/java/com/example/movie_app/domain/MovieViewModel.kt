package com.example.movie_app.domain

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.movie_app.BuildConfig
import com.example.movie_app.api.RetrofitInstance
import com.example.movie_app.data.DatabaseProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// DOMAIN LAYER — UiState
//
// Represents every possible state the UI can be in during a data fetch.
// Defined here in domain/ because it is business logic, not a UI concern
// and not a network/database concern.
// ---------------------------------------------------------------------------
sealed class UiState {
    /** A network refresh is in progress. */
    object Loading : UiState()

    /** The last refresh completed successfully. */
    object Success : UiState()

    /** The last refresh failed. [message] is surfaced to the user. */
    data class Error(val message: String) : UiState()
}

// ---------------------------------------------------------------------------
// DOMAIN LAYER — MovieViewModel
//
// Thin orchestration layer between the Repository and the UI.
// Responsibilities:
//  - Wire up the Repository (passes it the DAO and API it needs)
//  - Expose movies as a StateFlow the UI observes
//  - Expose uiState so the UI knows whether to show a spinner or error
//  - Expose refreshMovies() so the UI can trigger a retry
//
// Does NOT know about Room, Retrofit, or any network/DB detail.
// ---------------------------------------------------------------------------
class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository(
        dao = DatabaseProvider.getDatabase(application).movieDao(),
        api = RetrofitInstance.api
    )

    /** The live movie list — Room drives this reactively via Flow. */
    val movies = repository.getMovies()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Current fetch status — private mutable, public read-only. */
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        Log.d("MovieViewModel", "ViewModel initialized: $this")
        refreshMovies()
    }

    /** Fetches the latest popular movies from the network and syncs them into Room. */
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
