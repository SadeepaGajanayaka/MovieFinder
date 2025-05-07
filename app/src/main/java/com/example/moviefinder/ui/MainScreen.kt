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
// Importing Material3 components
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
// Importing Compose runtime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.sp
// Importing app resources
import com.example.moviefinder.R
// Importing ViewModel
import com.example.moviefinder.viewmodel.MainViewModel

// MainScreen composable function
// The main navigation hub of the app
@Composable
fun MainScreen(
    viewModel: MainViewModel,                  // ViewModel for the screen
    onAddMoviesClick: () -> Unit,             // Navigate to add movies screen
    onSearchMoviesClick: () -> Unit,          // Navigate to search movies screen
    onSearchActorsClick: () -> Unit,          // Navigate to search actors screen
    onSearchMoviesByTitleClick: () -> Unit    // Navigate to search movies by title screen
) {
    // Collect the movie count state
    val movieCount by viewModel.movieCount.collectAsState()

    // Get the current screen configuration
    val configuration = LocalConfiguration.current
    // Check if device is in landscape mode
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Box containing the entire screen content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Black background color
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
                // Title and welcome text column (left side)
                Column(
                    modifier = Modifier
                        .weight(1f) // Take 50% of row width
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()), // Make column scrollable
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // App title
                    Text(
                        text = "Movie Finder",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 32.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Welcome text
                    Text(
                        text = "Welcome to MovieFinder\nyour personal cinema companion for discovering and exploring movies!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Database movie count
                    Text(
                        text = "Database contains $movieCount movies",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Buttons column (right side)
                Column(
                    modifier = Modifier
                        .weight(1f) // Take 50% of row width
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()), // Make column scrollable
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Add Movies button
                    Button(
                        onClick = onAddMoviesClick, // Navigate to Add Movies screen
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = "Add Movies to DB",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Movies button
                    Button(
                        onClick = onSearchMoviesClick, // Navigate to Search Movies screen
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = "Search for Movies",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Actors button
                    Button(
                        onClick = onSearchActorsClick, // Navigate to Search Actors screen
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = "Search for Actors",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Movies by Title button
                    Button(
                        onClick = onSearchMoviesByTitleClick, // Navigate to Search Movies by Title screen
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = "Search Movie by Title",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // App title
                Text(
                    text = "Movie Finder",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 32.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Welcome text
                Text(
                    text = "Welcome to MovieFinder\nyour personal cinema companion for discovering and exploring movies!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Database movie count
                Text(
                    text = "Database contains $movieCount movies",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Add Movies button
                Button(
                    onClick = onAddMoviesClick, // Navigate to Add Movies screen
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // 80% of screen width
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    )
                ) {
                    Text(
                        text = "Add Movies to DB",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Movies button
                Button(
                    onClick = onSearchMoviesClick, // Navigate to Search Movies screen
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // 80% of screen width
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    )
                ) {
                    Text(
                        text = "Search for Movies",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Actors button
                Button(
                    onClick = onSearchActorsClick, // Navigate to Search Actors screen
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // 80% of screen width
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    )
                ) {
                    Text(
                        text = "Search for Actors",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Movies by Title button
                Button(
                    onClick = onSearchMoviesByTitleClick, // Navigate to Search Movies by Title screen
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // 80% of screen width
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    )
                ) {
                    Text(
                        text = "Search Movie by Title",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}