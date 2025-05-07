package com.example.moviefinder.viewmodel

// Importing ViewModel related classes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
// Importing MovieResponse model class
import com.example.moviefinder.data.remote.model.MovieResponse
// Importing the repository for data operations
import com.example.moviefinder.repository.MovieRepository
// Importing Flow related classes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
// Importing coroutines
import kotlinx.coroutines.launch

// ViewModel class for the Search Movies screen
// Manages UI state and business logic for movie search and saving
class SearchMoviesViewModel(private val repository: MovieRepository) : ViewModel() {

    // Private mutable state flow for search title
    private val _searchTitle = MutableStateFlow("")
    // Public immutable state flow that UI can observe
    val searchTitle: StateFlow<String> = _searchTitle.asStateFlow()

    // Private mutable state flow for UI state
    private val _uiState = MutableStateFlow<SearchMoviesUiState>(SearchMoviesUiState.Initial)
    // Public immutable state flow that UI can observe
    val uiState: StateFlow<SearchMoviesUiState> = _uiState.asStateFlow()

    // Function to update search title when user types
    fun updateSearchTitle(title: String) {
        _searchTitle.value = title
    }

    // Function to search for a specific movie in OMDB API
    fun searchMovie() {
        val title = _searchTitle.value
        // Validate input - ensure search title is not blank
        if (title.isBlank()) {
            _uiState.value = SearchMoviesUiState.Error("Please enter a movie title")
            return
        }

        // Launch coroutine in viewModelScope
        viewModelScope.launch {
            // Set state to loading to show progress indicator
            _uiState.value = SearchMoviesUiState.Loading
            try {
                // Call repository function to fetch movie by title
                val movie = repository.fetchMovieByTitle(title)
                // Check if API returned success response
                if (movie.response == "True") {
                    // If movie found, update state with movie details
                    _uiState.value = SearchMoviesUiState.Success(movie)
                } else {
                    // If no movie found, show error message
                    _uiState.value = SearchMoviesUiState.Error("Movie not found")
                }
            } catch (e: Exception) {
                // If an error occurs, update state with error message
                _uiState.value = SearchMoviesUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Function to save movie to local database
    fun saveMovieToDatabase() {
        // Get the current state to check if there's a movie to save
        val currentState = _uiState.value
        // Only proceed if a movie has been successfully loaded
        if (currentState is SearchMoviesUiState.Success) {
            // Launch coroutine in viewModelScope
            viewModelScope.launch {
                try {
                    // Call repository function to save movie and its actors
                    repository.saveMovieFromResponse(currentState.movie)
                    // Update state to indicate successful save
                    _uiState.value = SearchMoviesUiState.SaveSuccess(currentState.movie)
                } catch (e: Exception) {
                    // If an error occurs, update state with error message
                    _uiState.value = SearchMoviesUiState.Error(e.message ?: "Failed to save movie")
                }
            }
        }
    }
}

// Sealed class for UI state
// Defines all possible states of the UI
sealed class SearchMoviesUiState {
    // Initial state when screen loads
    object Initial : SearchMoviesUiState()
    // Loading state while searching
    object Loading : SearchMoviesUiState()
    // Success state with movie details
    data class Success(val movie: MovieResponse) : SearchMoviesUiState()
    // Save success state with movie details
    data class SaveSuccess(val movie: MovieResponse) : SearchMoviesUiState()
    // Error state with message
    data class Error(val message: String) : SearchMoviesUiState()
}

// Factory class for creating SearchMoviesViewModel instances
// Needed because ViewModel requires repository dependency
class SearchMoviesViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    // Override create method to return our ViewModel instance
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if requested class is our ViewModel class
        if (modelClass.isAssignableFrom(SearchMoviesViewModel::class.java)) {
            // Cast to T and return the ViewModel instance
            @Suppress("UNCHECKED_CAST")
            return SearchMoviesViewModel(repository) as T
        }
        // Throw exception if requested class is not our ViewModel
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}