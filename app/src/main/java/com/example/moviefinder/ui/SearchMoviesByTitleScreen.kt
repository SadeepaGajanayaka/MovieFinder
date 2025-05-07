package com.example.moviefinder.ui

// Importing Compose UI components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// Importing Material Design icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
// Importing Material3 components
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
// Importing Compose runtime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
// Importing UI-related components
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// Importing ViewModel components
import androidx.lifecycle.viewmodel.compose.viewModel
// Importing app resources and models
import com.example.moviefinder.R
import com.example.moviefinder.data.remote.model.SearchResult
import com.example.moviefinder.repository.MovieRepository
import com.example.moviefinder.viewmodel.SearchMoviesByTitleUiState
import com.example.moviefinder.viewmodel.SearchMoviesByTitleViewModel
import com.example.moviefinder.viewmodel.SearchMoviesByTitleViewModelFactory
// Importing coroutines
import kotlinx.coroutines.launch

// SearchMoviesByTitleScreen composable function
// Handles UI for searching movies by title in OMDB API
@OptIn(ExperimentalMaterial3Api::class) // Needed for TopAppBar
@Composable
fun SearchMoviesByTitleScreen(
    repository: MovieRepository,    // Repository for database and API operations
    onNavigateBack: () -> Unit      // Callback for navigation
) {
    // Create ViewModel instance with factory
    val viewModel: SearchMoviesByTitleViewModel = viewModel(
        factory = SearchMoviesByTitleViewModelFactory(repository)
    )
    // Collect search term from ViewModel as state
    val searchTerm by viewModel.searchTerm.collectAsState()
    // Collect UI state from ViewModel as state
    val uiState by viewModel.uiState.collectAsState()
    // Create coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()

    // Get the current screen configuration
    val configuration = LocalConfiguration.current
    // Check if device is in landscape mode
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Scaffold provides basic Material Design layout structure
    Scaffold(
        topBar = {
            // Top app bar with back button
            TopAppBar(
                title = { Text("Search Movies by Title", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black // Black background for app bar
                )
            )
        },
        containerColor = Color.Black // Black background for scaffold
    ) { paddingValues ->
        // Box containing the entire screen content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from scaffold
        ) {
            // Background image with movie posters
            Image(
                painter = painterResource(id = R.drawable.movie_posters_background),
                contentDescription = "Movie posters background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop, // Crop image to fill space
                alpha = 0.3f // Make image partially transparent
            )

            // Check device orientation and apply appropriate layout
            if (isLandscape) {
                // Landscape layout with content in Row
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Left side - search form
                    Column(
                        modifier = Modifier
                            .weight(1f) // Take 50% of row width
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()) // Make column scrollable
                    ) {
                        // Screen title
                        Text(
                            text = "Search for movies in OMDB",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Search text field
                        OutlinedTextField(
                            value = searchTerm, // Current search term
                            onValueChange = { viewModel.updateSearchTerm(it) }, // Update search term
                            label = { Text("Enter search term", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.Gray,
                                cursorColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Search button
                        Button(
                            onClick = {
                                // Launch coroutine to search movies
                                coroutineScope.launch {
                                    viewModel.searchMoviesByTitle()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray
                            )
                        ) {
                            Text(
                                text = "Search",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right side - results
                    Box(
                        modifier = Modifier
                            .weight(1f) // Take 50% of row width
                            .fillMaxHeight()
                    ) {
                        // Show different UI based on current state
                        when (uiState) {
                            // Loading state - show progress indicator
                            is SearchMoviesByTitleUiState.Loading -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Searching for movies...", color = Color.White)
                                }
                            }
                            // Success state - show movie list or empty message
                            is SearchMoviesByTitleUiState.Success -> {
                                val movies = (uiState as SearchMoviesByTitleUiState.Success).movies
                                if (movies.isEmpty()) {
                                    // No movies found
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No movies found matching your search",
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    // Display list of movies
                                    LazyColumn {
                                        items(movies) { movie ->
                                            SearchResultCard(movie = movie) // Custom card for each movie
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                            // Error state - show error message
                            is SearchMoviesByTitleUiState.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Error: ${(uiState as SearchMoviesByTitleUiState.Error).message}",
                                        color = Color.Red,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            // Initial state - show instructions
                            else -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Enter search terms and press 'Search'",
                                        color = Color.LightGray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Portrait layout with content in Column
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Screen title
                    Text(
                        text = "Search for movies in OMDB",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Search text field
                    OutlinedTextField(
                        value = searchTerm, // Current search term
                        onValueChange = { viewModel.updateSearchTerm(it) }, // Update search term
                        label = { Text("Enter search term", color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search button
                    Button(
                        onClick = {
                            // Launch coroutine to search movies
                            coroutineScope.launch {
                                viewModel.searchMoviesByTitle()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = "Search",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Results area - takes remaining space
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Expand to fill available space
                    ) {
                        // Show different UI based on current state
                        when (uiState) {
                            // Loading state - show progress indicator
                            is SearchMoviesByTitleUiState.Loading -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Searching for movies...", color = Color.White)
                                }
                            }
                            // Success state - show movie list or empty message
                            is SearchMoviesByTitleUiState.Success -> {
                                val movies = (uiState as SearchMoviesByTitleUiState.Success).movies
                                if (movies.isEmpty()) {
                                    // No movies found
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No movies found matching your search",
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    // Display list of movies
                                    LazyColumn {
                                        items(movies) { movie ->
                                            SearchResultCard(movie = movie) // Custom card for each movie
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                            // Error state - show error message
                            is SearchMoviesByTitleUiState.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Error: ${(uiState as SearchMoviesByTitleUiState.Error).message}",
                                        color = Color.Red,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            // Initial state - show instructions
                            else -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Enter search terms and press 'Search'",
                                        color = Color.LightGray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper composable for displaying search result card
// Used in the search results list to display movie info
@Composable
fun SearchResultCard(movie: SearchResult) {
    // Card with rounded corners and darker background
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D2D2D) // Dark gray background
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        // Column for movie details
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Movie title
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Movie details
            Text("Year: ${movie.year}", color = Color.LightGray)
            Text("Type: ${movie.type}", color = Color.LightGray)
            Text("IMDB ID: ${movie.imdbID}", color = Color.LightGray)
        }
    }
}