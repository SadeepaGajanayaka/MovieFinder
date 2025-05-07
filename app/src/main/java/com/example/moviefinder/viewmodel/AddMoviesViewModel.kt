package com.example.moviefinder.viewmodel

// Importing ViewModel related classes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
// Importing the repository for data operations
import com.example.moviefinder.repository.MovieRepository
// Importing Flow related classes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
// Importing coroutines
import kotlinx.coroutines.launch

// ViewModel class for the Add Movies screen
// Manages UI state and business logic
class AddMoviesViewModel(private val repository: MovieRepository) : ViewModel() {

    // Private mutable state flow to update UI state internally
    private val _uiState = MutableStateFlow<AddMoviesUiState>(AddMoviesUiState.Initial)
    // Public immutable state flow that UI can observe
    val uiState: StateFlow<AddMoviesUiState> = _uiState.asStateFlow()

    // Function to add predefined movies to the database
    fun addPredefinedMovies() {
        // Launch coroutine in viewModelScope
        // This ensures the coroutine is cancelled when ViewModel is cleared
        viewModelScope.launch {
            // Set state to loading to show progress indicator
            _uiState.value = AddMoviesUiState.Loading
            try {
                // Call repository function to add predefined movies
                repository.addPredefinedMoviesIfNotExists()
                // Update state to success
                _uiState.value = AddMoviesUiState.Success
            } catch (e: Exception) {
                // If an error occurs, update state with error message
                _uiState.value = AddMoviesUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}

// Sealed class for UI state
// Defines all possible states of the UI
sealed class AddMoviesUiState {
    // Initial state when screen loads
    object Initial : AddMoviesUiState()
    // Loading state while adding movies
    object Loading : AddMoviesUiState()
    // Success state after movies added
    object Success : AddMoviesUiState()
    // Error state with message
    data class Error(val message: String) : AddMoviesUiState()
}

// Factory class for creating AddMoviesViewModel instances
// Needed because ViewModel requires repository dependency
class AddMoviesViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    // Override create method to return our ViewModel instance
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if requested class is our ViewModel class
        if (modelClass.isAssignableFrom(AddMoviesViewModel::class.java)) {
            // Cast to T and return the ViewModel instance
            @Suppress("UNCHECKED_CAST")
            return AddMoviesViewModel(repository) as T
        }
        // Throw exception if requested class is not our ViewModel
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}