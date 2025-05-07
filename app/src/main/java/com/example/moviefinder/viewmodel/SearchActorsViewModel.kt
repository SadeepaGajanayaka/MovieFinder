package com.example.moviefinder.viewmodel

// Importing ViewModel related classes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
// Importing Movie entity class
import com.example.moviefinder.data.local.entity.Movie
// Importing the repository for data operations
import com.example.moviefinder.repository.MovieRepository
// Importing Flow related classes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
// Importing coroutines
import kotlinx.coroutines.launch

// ViewModel class for the Search Actors screen
// Manages UI state and business logic for actor search
class SearchActorsViewModel(private val repository: MovieRepository) : ViewModel() {

    // Private mutable state flow for search term
    private val _searchTerm = MutableStateFlow("")
    // Public immutable state flow that UI can observe
    val searchTerm: StateFlow<String> = _searchTerm.asStateFlow()

    // Private mutable state flow for UI state
    private val _uiState = MutableStateFlow<SearchActorsUiState>(SearchActorsUiState.Initial)
    // Public immutable state flow that UI can observe
    val uiState: StateFlow<SearchActorsUiState> = _uiState.asStateFlow()

    // Function to update search term when user types
    fun updateSearchTerm(term: String) {
        _searchTerm.value = term
    }

    // Function to search actors and get associated movies
    fun searchActors() {
        val term = _searchTerm.value
        // Validate input - ensure search term is not blank
        if (term.isBlank()) {
            _uiState.value = SearchActorsUiState.Error("Please enter an actor name")
            return
        }

        // Launch coroutine in viewModelScope
        viewModelScope.launch {
            // Set state to loading to show progress indicator
            _uiState.value = SearchActorsUiState.Loading
            try {
                // Call repository function to get movies by actor name
                // Collect updates from Flow
                repository.getMoviesByActorName(term).collect { movies ->
                    if (movies.isNotEmpty()) {
                        // If movies found, update state with movie list
                        _uiState.value = SearchActorsUiState.Success(movies)
                    } else {
                        // If no movies found, show error message
                        _uiState.value = SearchActorsUiState.Error("No movies found with this actor")
                    }
                }
            } catch (e: Exception) {
                // If an error occurs, update state with error message
                _uiState.value = SearchActorsUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

// Sealed class for UI state
// Defines all possible states of the UI
sealed class SearchActorsUiState {
    // Initial state when screen loads
    object Initial : SearchActorsUiState()
    // Loading state while searching
    object Loading : SearchActorsUiState()
    // Success state with movie list
    data class Success(val movies: List<Movie>) : SearchActorsUiState()
    // Error state with message
    data class Error(val message: String) : SearchActorsUiState()
}

// Factory class for creating SearchActorsViewModel instances
// Needed because ViewModel requires repository dependency
class SearchActorsViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    // Override create method to return our ViewModel instance
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if requested class is our ViewModel class
        if (modelClass.isAssignableFrom(SearchActorsViewModel::class.java)) {
            // Cast to T and return the ViewModel instance
            @Suppress("UNCHECKED_CAST")
            return SearchActorsViewModel(repository) as T
        }
        // Throw exception if requested class is not our ViewModel
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}