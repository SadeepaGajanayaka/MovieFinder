package com.example.moviefinder.viewmodel

// Importing ViewModel related classes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
// Importing SearchResult model class
import com.example.moviefinder.data.remote.model.SearchResult
// Importing the repository for data operations
import com.example.moviefinder.repository.MovieRepository
// Importing Flow related classes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
// Importing coroutines
import kotlinx.coroutines.launch

// ViewModel class for the Search Movies by Title screen
// Manages UI state and business logic for movie search
class SearchMoviesByTitleViewModel(private val repository: MovieRepository) : ViewModel() {

    // Private mutable state flow for search term
    private val _searchTerm = MutableStateFlow("")
    // Public immutable state flow that UI can observe
    val searchTerm: StateFlow<String> = _searchTerm.asStateFlow()

    // Private mutable state flow for UI state
    private val _uiState = MutableStateFlow<SearchMoviesByTitleUiState>(SearchMoviesByTitleUiState.Initial)
    // Public immutable state flow that UI can observe
    val uiState: StateFlow<SearchMoviesByTitleUiState> = _uiState.asStateFlow()

    // Function to update search term when user types
    fun updateSearchTerm(term: String) {
        _searchTerm.value = term
    }

    // Function to search movies by title in OMDB API
    fun searchMoviesByTitle() {
        val term = _searchTerm.value
        // Validate input - ensure search term is not blank
        if (term.isBlank()) {
            _uiState.value = SearchMoviesByTitleUiState.Error("Please enter a search term")
            return
        }

        // Launch coroutine in viewModelScope
        viewModelScope.launch {
            // Set state to loading to show progress indicator
            _uiState.value = SearchMoviesByTitleUiState.Loading
            try {
                // Call repository function to search movies in OMDB API
                val searchResponse = repository.searchMovies(term)
                // Check if API returned success response
                if (searchResponse.response == "True") {
                    // If search successful, update state with movie list
                    _uiState.value = SearchMoviesByTitleUiState.Success(searchResponse.search)
                } else {
                    // If no movies found, show error message
                    _uiState.value = SearchMoviesByTitleUiState.Error("No movies found with this title")
                }
            } catch (e: Exception) {
                // If an error occurs, update state with error message
                _uiState.value = SearchMoviesByTitleUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

// Sealed class for UI state
// Defines all possible states of the UI
sealed class SearchMoviesByTitleUiState {
    // Initial state when screen loads
    object Initial : SearchMoviesByTitleUiState()
    // Loading state while searching
    object Loading : SearchMoviesByTitleUiState()
    // Success state with movie list
    data class Success(val movies: List<SearchResult>) : SearchMoviesByTitleUiState()
    // Error state with message
    data class Error(val message: String) : SearchMoviesByTitleUiState()
}

// Factory class for creating SearchMoviesByTitleViewModel instances
// Needed because ViewModel requires repository dependency
class SearchMoviesByTitleViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    // Override create method to return our ViewModel instance
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if requested class is our ViewModel class
        if (modelClass.isAssignableFrom(SearchMoviesByTitleViewModel::class.java)) {
            // Cast to T and return the ViewModel instance
            @Suppress("UNCHECKED_CAST")
            return SearchMoviesByTitleViewModel(repository) as T
        }
        // Throw exception if requested class is not our ViewModel
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}