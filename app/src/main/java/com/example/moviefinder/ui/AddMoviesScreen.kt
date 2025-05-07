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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
// Importing Material Design icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
// Importing Material3 components
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
// Importing app resources
import com.example.moviefinder.R
// Importing repository and ViewModel
import com.example.moviefinder.repository.MovieRepository
import com.example.moviefinder.viewmodel.AddMoviesUiState
import com.example.moviefinder.viewmodel.AddMoviesViewModel
import com.example.moviefinder.viewmodel.AddMoviesViewModelFactory
// Importing coroutines
import kotlinx.coroutines.launch

// AddMoviesScreen composable function
// Handles UI for adding predefined movies to the database
@OptIn(ExperimentalMaterial3Api::class) // Needed for TopAppBar
@Composable
fun AddMoviesScreen(
    repository: MovieRepository,       // Repository for database operations
    onNavigateBack: () -> Unit         // Callback for navigation
) {
    // Create ViewModel instance with factory
    val viewModel: AddMoviesViewModel = viewModel(
        factory = AddMoviesViewModelFactory(repository)
    )
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
                title = { Text("Add Movies", color = Color.White) },
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
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left side - title and description
                    Column(
                        modifier = Modifier
                            .weight(1f) // Take 50% of row width
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()), // Make column scrollable
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Screen title
                        Text(
                            text = "Add Predefined Movies to Database",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description text
                        Text(
                            text = "Click the button to add a collection of classic movies to your local database.",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right side - action content (buttons, status)
                    Box(
                        modifier = Modifier
                            .weight(1f) // Take 50% of row width
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Show different UI based on current state
                        when (uiState) {
                            // Initial state - show add button
                            is AddMoviesUiState.Initial -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(
                                        onClick = {
                                            // Launch coroutine to add movies
                                            coroutineScope.launch {
                                                viewModel.addPredefinedMovies()
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.LightGray
                                        )
                                    ) {
                                        Text(
                                            text = "Add Movies",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            // Loading state - show progress indicator
                            is AddMoviesUiState.Loading -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(color = Color.White)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Adding movies to database...", color = Color.White)
                                }
                            }
                            // Success state - show success message and back button
                            is AddMoviesUiState.Success -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Movies Added Successfully!",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(32.dp))

                                    Button(
                                        onClick = onNavigateBack,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.LightGray
                                        )
                                    ) {
                                        Text(
                                            text = "Back to Main Menu",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            // Error state - show error message and retry button
                            is AddMoviesUiState.Error -> {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Error: ${(uiState as AddMoviesUiState.Error).message}",
                                        color = Color.Red,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(32.dp))

                                    Button(
                                        onClick = {
                                            coroutineScope.launch {
                                                viewModel.addPredefinedMovies()
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.LightGray
                                        )
                                    ) {
                                        Text(
                                            text = "Try Again",
                                            color = Color.Black,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
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
                        .verticalScroll(rememberScrollState()), // Make column scrollable
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Screen title
                    Text(
                        text = "Add Predefined Movies to Database",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Show different UI based on current state
                    when (uiState) {
                        // Initial state - show description and add button
                        is AddMoviesUiState.Initial -> {
                            Text(
                                text = "Click the button below to add a collection of classic movies to your local database.",
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.addPredefinedMovies()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f) // 80% of screen width
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.LightGray
                                )
                            ) {
                                Text(
                                    text = "Add Movies",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        // Loading state - show progress indicator
                        is AddMoviesUiState.Loading -> {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Adding movies to database...", color = Color.White)
                        }
                        // Success state - show success message and back button
                        is AddMoviesUiState.Success -> {
                            Text(
                                text = "Movies Added Successfully!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = onNavigateBack,
                                modifier = Modifier
                                    .fillMaxWidth(0.8f) // 80% of screen width
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.LightGray
                                )
                            ) {
                                Text(
                                    text = "Back to Main Menu",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        // Error state - show error message and retry button
                        is AddMoviesUiState.Error -> {
                            Text(
                                text = "Error: ${(uiState as AddMoviesUiState.Error).message}",
                                color = Color.Red,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.addPredefinedMovies()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f) // 80% of screen width
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.LightGray
                                )
                            ) {
                                Text(
                                    text = "Try Again",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}