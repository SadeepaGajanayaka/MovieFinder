package com.example.moviefinder

// Importing Android components
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// Importing Compose UI components
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
// Importing lifecycle and coroutine components
import androidx.lifecycle.lifecycleScope
// Importing navigation components
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Importing app database and API service
import com.example.moviefinder.data.local.MovieDatabase
import com.example.moviefinder.data.remote.OmdbApiService
// Importing repositories
import com.example.moviefinder.repository.ActorRepository
import com.example.moviefinder.repository.MovieRepository
// Importing UI screens
import com.example.moviefinder.ui.AddMoviesScreen
import com.example.moviefinder.ui.MainScreen
import com.example.moviefinder.ui.SearchActorsScreen
import com.example.moviefinder.ui.SearchMoviesByTitleScreen
import com.example.moviefinder.ui.SearchMoviesScreen
import com.example.moviefinder.ui.theme.MovieQuizTheme
// Importing coroutines
import kotlinx.coroutines.launch
// Importing ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviefinder.viewmodel.MainViewModel
import com.example.moviefinder.viewmodel.MainViewModelFactory

// Main activity class - entry point of the application
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and DAO objects
        val database = MovieDatabase.getDatabase(this)
        val movieDao = database.movieDao()
        val actorDao = database.actorDao()
        // Initialize API service for OMDB
        val apiService = OmdbApiService.getInstance()

        // Create repositories that will handle data operations
        val actorRepository = ActorRepository(actorDao)
        val repository = MovieRepository(movieDao, actorDao, apiService)

        // Run database cleanup on app startup
        // This removes any duplicate movies that might exist
        lifecycleScope.launch {
            repository.removeDuplicateMovies()
        }

        // Set up the Compose UI content
        setContent {
            // Apply the app theme
            MovieQuizTheme {
                // Surface container for the entire app
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create navigation controller for screen navigation
                    val navController = rememberNavController()
                    // Set up navigation with our screens
                    AppNavHost(navController, repository)
                }
            }
        }
    }
}

// AppNavHost composable function
// Defines the navigation structure of the app
@Composable
fun AppNavHost(navController: NavHostController, repository: MovieRepository) {
    // NavHost sets up the navigation framework
    NavHost(
        navController = navController,
        startDestination = "main"  // Define the starting screen
    ) {
        // Main screen - app's home screen
        composable("main") {
            // Create ViewModel for the main screen
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(repository)
            )

            MainScreen(
                viewModel = mainViewModel,
                // Navigation callbacks for each button
                onAddMoviesClick = { navController.navigate("add_movies") },
                onSearchMoviesClick = { navController.navigate("search_movies") },
                onSearchActorsClick = { navController.navigate("search_actors") },
                onSearchMoviesByTitleClick = { navController.navigate("search_movies_by_title") }
            )
        }

        // Add Movies screen - for adding predefined movies to database
        composable("add_movies") {
            AddMoviesScreen(
                repository = repository,
                onNavigateBack = { navController.popBackStack() }  // Go back to previous screen
            )
        }

        // Search Movies screen - for searching specific movies
        composable("search_movies") {
            SearchMoviesScreen(
                repository = repository,
                onNavigateBack = { navController.popBackStack() }  // Go back to previous screen
            )
        }

        // Search Actors screen - for finding movies by actor
        composable("search_actors") {
            SearchActorsScreen(
                repository = repository,
                onNavigateBack = { navController.popBackStack() }  // Go back to previous screen
            )
        }

        // Search Movies by Title screen - for general movie search
        composable("search_movies_by_title") {
            SearchMoviesByTitleScreen(
                repository = repository,
                onNavigateBack = { navController.popBackStack() }  // Go back to previous screen
            )
        }
    }
}