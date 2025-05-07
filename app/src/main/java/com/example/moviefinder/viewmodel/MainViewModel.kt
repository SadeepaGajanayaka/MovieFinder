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

// ViewModel class for the Main screen
// Manages business logic for main navigation hub
class MainViewModel(private val repository: MovieRepository) : ViewModel() {

    // State flow for movie count
    private val _movieCount = MutableStateFlow(0)
    val movieCount: StateFlow<Int> = _movieCount.asStateFlow()

    // Initialize the ViewModel
    init {
        // Load movie count when ViewModel is created
        loadMovieCount()
    }

    // Function to load the movie count
    private fun loadMovieCount() {
        viewModelScope.launch {
            repository.getAllMovies().collect { movies ->
                _movieCount.value = movies.size
            }
        }
    }

    // Functions for navigation
    // These are placeholders as navigation is handled in the UI layer
    // In a more complex app, they might perform data loading or preparation

    // Navigate to Add Movies screen
    fun navigateToAddMovies() {
        // Navigation will be handled in the UI layer
    }

    // Navigate to Search Movies screen
    fun navigateToSearchMovies() {
        // Navigation will be handled in the UI layer
    }

    // Navigate to Search Actors screen
    fun navigateToSearchActors() {
        // Navigation will be handled in the UI layer
    }

    // Navigate to Search Movies by Title screen
    fun navigateToSearchMoviesByTitle() {
        // Navigation will be handled in the UI layer
    }
}

// Factory class for creating MainViewModel instances
// Needed because ViewModel requires repository dependency
class MainViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    // Override create method to return our ViewModel instance
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if requested class is our ViewModel class
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            // Cast to T and return the ViewModel instance
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        // Throw exception if requested class is not our ViewModel
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}