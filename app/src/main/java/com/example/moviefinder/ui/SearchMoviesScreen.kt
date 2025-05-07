package com.example.moviefinder.ui

// Importing Compose UI components
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
// Importing UI-related components
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
// Importing ViewModel components
import androidx.lifecycle.viewmodel.compose.viewModel
// Importing app resources and models
import com.example.moviefinder.R
import com.example.moviefinder.data.remote.model.MovieResponse
import com.example.moviefinder.repository.MovieRepository
import com.example.moviefinder.viewmodel.SearchMoviesUiState
import com.example.moviefinder.viewmodel.SearchMoviesViewModel
import com.example.moviefinder.viewmodel.SearchMoviesViewModelFactory
// Importing coroutines
import kotlinx.coroutines.launch
// Importing layout arrangement
import androidx.compose.foundation.layout.Arrangement

// SearchMoviesScreen composable function
// Handles UI for searching and saving movies from OMDB API
@OptIn(ExperimentalMaterial3Api::class) // Needed for TopAppBar
@Composable
fun SearchMoviesScreen(
    repository: MovieRepository,    // Repository for database operations
    onNavigateBack: () -> Unit      // Callback for navigation
) {
    // Create ViewModel instance with factory
    val viewModel: SearchMoviesViewModel = viewModel(
        factory = SearchMoviesViewModelFactory(repository)
    )
    // Collect search title from ViewModel as state
    val searchTitle by viewModel.searchTitle.collectAsState()
    // Collect UI state from ViewModel as state
    val uiState by viewModel.uiState.collectAsState()
    // Create coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    // Local state for showing success message
    var showSuccessMessage by remember { mutableStateOf(false) }

    // Get the current screen configuration
    val configuration = LocalConfiguration.current
    // Check if device is in landscape mode
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Side effect to show success message when movie is saved
    LaunchedEffect(uiState) {
        if (uiState is SearchMoviesUiState.SaveSuccess) {
            showSuccessMessage = true
        }
    }

    // Scaffold provides basic Material Design layout structure
    Scaffold(
        topBar = {
            // Top app bar with back button
            TopAppBar(
                title = { Text("Search Movies", color = Color.White) },
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
                            text = "Search for movie details",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Search text field
                        OutlinedTextField(
                            value = searchTitle, // Current search title
                            onValueChange = { viewModel.updateSearchTitle(it) }, // Update search title
                            label = { Text("Enter movie title", color = Color.LightGray) },
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

                        // Buttons row - retrieve and save
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Retrieve Movie button
                            Button(
                                onClick = {
                                    // Launch coroutine to search movie
                                    coroutineScope.launch {
                                        viewModel.searchMovie()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f) // Take 50% of row width
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.LightGray
                                )
                            ) {
                                Text(
                                    text = "Retrieve Movie",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Save Movie button
                            Button(
                                onClick = {
                                    // Launch coroutine to save movie
                                    coroutineScope.launch {
                                        viewModel.saveMovieToDatabase()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f) // Take 50% of row width
                                    .height(50.dp),
                                // Only enable if movie loaded successfully
                                enabled = uiState is SearchMoviesUiState.Success,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.LightGray,
                                    disabledContainerColor = Color.DarkGray
                                )
                            ) {
                                Text(
                                    text = "Save Movie",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Show success message if movie saved
                        if (showSuccessMessage && uiState is SearchMoviesUiState.SaveSuccess) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Movie successfully saved to database!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Green,
                                modifier = Modifier.padding(bottom = 16.dp)
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
                            is SearchMoviesUiState.Loading -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Searching for movie...", color = Color.White)
                                }
                            }
                            // Success state - show movie details
                            is SearchMoviesUiState.Success -> {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()), // Make card scrollable
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF2D2D2D) // Dark gray background
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    MovieDetails(movie = (uiState as SearchMoviesUiState.Success).movie)
                                }
                            }
                            // Save success state - show movie details
                            is SearchMoviesUiState.SaveSuccess -> {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()), // Make card scrollable
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF2D2D2D) // Dark gray background
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    MovieDetails(movie = (uiState as SearchMoviesUiState.SaveSuccess).movie)
                                }
                            }
                            // Error state - show error message
                            is SearchMoviesUiState.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Error: ${(uiState as SearchMoviesUiState.Error).message}",
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
                                        text = "Enter a movie title and press 'Retrieve Movie'",
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
                        text = "Search for movie details",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Search text field
                    OutlinedTextField(
                        value = searchTitle, // Current search title
                        onValueChange = { viewModel.updateSearchTitle(it) }, // Update search title
                        label = { Text("Enter movie title", color = Color.LightGray) },
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

                    // Buttons row - retrieve and save
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Retrieve Movie button
                        Button(
                            onClick = {
                                // Launch coroutine to search movie
                                coroutineScope.launch {
                                    viewModel.searchMovie()
                                }
                            },
                            modifier = Modifier
                                .weight(1f) // Take 50% of row width
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray
                            )
                        ) {
                            Text(
                                text = "Retrieve Movie",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Save Movie button
                        Button(
                            onClick = {
                                // Launch coroutine to save movie
                                coroutineScope.launch {
                                    viewModel.saveMovieToDatabase()
                                }
                            },
                            modifier = Modifier
                                .weight(1f) // Take 50% of row width
                                .height(50.dp),
                            // Only enable if movie loaded successfully
                            enabled = uiState is SearchMoviesUiState.Success,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray,
                                disabledContainerColor = Color.DarkGray
                            )
                        ) {
                            Text(
                                text = "Save Movie",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                            is SearchMoviesUiState.Loading -> {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Searching for movie...", color = Color.White)
                                }
                            }
                            // Success state - show movie details
                            is SearchMoviesUiState.Success -> {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState()), // Make card scrollable
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF2D2D2D) // Dark gray background
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    MovieDetails(movie = (uiState as SearchMoviesUiState.Success).movie)
                                }
                            }
                            // Save success state - show success message and movie details
                            is SearchMoviesUiState.SaveSuccess -> {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    // Show success message if movie saved
                                    if (showSuccessMessage) {
                                        Text(
                                            "Movie successfully saved to database!",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color.Green,
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )
                                    }

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(rememberScrollState()), // Make card scrollable
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color(0xFF2D2D2D) // Dark gray background
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        MovieDetails(movie = (uiState as SearchMoviesUiState.SaveSuccess).movie)
                                    }
                                }
                            }
                            // Error state - show error message
                            is SearchMoviesUiState.Error -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Error: ${(uiState as SearchMoviesUiState.Error).message}",
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
                                        text = "Enter a movie title and press 'Retrieve Movie'",
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

// Helper composable for displaying movie details
// Used to show movie information from API response
@Composable
fun MovieDetails(movie: MovieResponse) {
    // Column for movie details
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Display each movie detail using custom row
        MovieDetailRow("Title", movie.title)
        MovieDetailRow("Year", movie.year)
        MovieDetailRow("Rated", movie.rated)
        MovieDetailRow("Released", movie.released)
        MovieDetailRow("Runtime", movie.runtime)
        MovieDetailRow("Genre", movie.genre)
        MovieDetailRow("Director", movie.director)
        MovieDetailRow("Writer", movie.writer)
        MovieDetailRow("Actors", movie.actors)
        MovieDetailRow("Plot", movie.plot)
    }
}

// Helper composable for displaying a single detail row
// Used for consistent formatting of movie details
@Composable
fun MovieDetailRow(label: String, value: String) {
    // Column for label and value
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Label text
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        // Value text
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            overflow = TextOverflow.Visible, // Allow text to wrap
            color = Color.LightGray
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}