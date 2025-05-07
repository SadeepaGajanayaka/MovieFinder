package com.example.moviefinder.repository

// Importing DAOs for database operations
import com.example.moviefinder.data.local.MovieDao
import com.example.moviefinder.data.local.ActorDao
// Importing entity classes
import com.example.moviefinder.data.local.entity.Actor
import com.example.moviefinder.data.local.entity.Movie
// Importing API service for network operations
import com.example.moviefinder.data.remote.OmdbApiService
// Importing API response data classes
import com.example.moviefinder.data.remote.model.MovieResponse
import com.example.moviefinder.data.remote.model.SearchResponse
// Importing Flow for reactive data streams
import kotlinx.coroutines.flow.Flow

// Repository class for Movie-related operations
// Coordinates data operations between different sources (local database and remote API)
class MovieRepository(
    private val movieDao: MovieDao,           // For local database operations on movies
    private val actorDao: ActorDao,           // For local database operations on actors
    private val omdbApiService: OmdbApiService // For remote API operations
) {
    // --- Local database operations ---

    // Insert a single movie into the database
    // Returns the ID of the inserted movie
    suspend fun insertMovie(movie: Movie): Long {
        return movieDao.insertMovie(movie)
    }

    // Insert multiple movies into the database
    // Useful for batch operations
    suspend fun insertMovies(movies: List<Movie>) {
        movieDao.insertMovies(movies)
    }

    // Get all movies as a Flow
    // Flow allows observing data changes over time
    fun getAllMovies(): Flow<List<Movie>> {
        return movieDao.getAllMovies()
    }

    // Search for movies by actor name
    // Returns a Flow of matching movies
    fun getMoviesByActorName(actorName: String): Flow<List<Movie>> {
        return movieDao.getMoviesByActorName(actorName)
    }

    // Find a movie by its exact title
    // Returns null if no matching movie found
    suspend fun getMovieByTitle(title: String): Movie? {
        return movieDao.getMovieByTitle(title)
    }

    // --- Actor management ---

    // Helper function to save actors for a movie
    // Avoids duplicating actors that already exist
    private suspend fun saveActorsForMovie(movieId: Long, actorsString: String) {
        // Get existing actors for this movie
        val existingActors = actorDao.getActorsByMovieIdSync(movieId)
        val existingActorNames = existingActors.map { it.name.lowercase() }

        // Parse actor names from the string
        val actorNames = actorsString.split(",").map { it.trim() }

        // Filter out actors that already exist for this movie
        val newActors = actorNames.filter { actorName ->
            actorName.isNotEmpty() && !existingActorNames.contains(actorName.lowercase())
        }.map { name ->
            Actor(
                name = name,
                movieId = movieId
            )
        }

        // Save only the new actors
        if (newActors.isNotEmpty()) {
            actorDao.insertActors(newActors)
        }
    }

    // Method to insert movie and its actors
    // Handles checking for duplicates
    suspend fun insertMovieWithActors(movie: Movie): Long {
        // Check if movie already exists
        val existingMovie = movieDao.getMovieByTitle(movie.title)
        if (existingMovie != null) {
            // Movie exists, just add any new actors
            saveActorsForMovie(existingMovie.id, movie.actors)
            return existingMovie.id
        }

        // Movie doesn't exist, create it with actors
        val movieId = movieDao.insertMovie(movie)
        saveActorsForMovie(movieId, movie.actors)
        return movieId
    }

    // --- Remote API operations ---

    // Fetch movie details from OMDB API by title
    suspend fun fetchMovieByTitle(title: String): MovieResponse {
        return omdbApiService.getMovieByTitle(title, OmdbApiService.API_KEY)
    }

    // Search for movies in OMDB API
    // Optional page parameter for pagination
    suspend fun searchMovies(searchTerm: String, page: Int = 1): SearchResponse {
        return omdbApiService.searchMovies(searchTerm, OmdbApiService.API_KEY, page)
    }

    // Save movie from API response to local database
    // Returns the ID of the saved movie
    suspend fun saveMovieFromResponse(response: MovieResponse): Long {
        // Check if the movie already exists
        val existingMovie = movieDao.getMovieByTitle(response.title)
        if (existingMovie != null) {
            // Movie already exists, just add any new actors
            saveActorsForMovie(existingMovie.id, response.actors)
            return existingMovie.id
        }

        // Movie doesn't exist, create a new one
        val movie = mapResponseToEntity(response)
        val movieId = movieDao.insertMovie(movie)
        saveActorsForMovie(movieId, response.actors)
        return movieId
    }

    // Convert API response to database entity
    fun mapResponseToEntity(response: MovieResponse): Movie {
        return Movie(
            title = response.title,
            year = response.year,
            rated = response.rated,
            released = response.released,
            runtime = response.runtime,
            genre = response.genre,
            director = response.director,
            writer = response.writer,
            actors = response.actors,
            plot = response.plot,
            language = response.language,
            country = response.country,
            awards = response.awards,
            poster = response.poster,
            imdbRating = response.imdbRating,
            imdbVotes = response.imdbVotes,
            imdbID = response.imdbID,
            type = response.type
        )
    }

    // Add predefined movies if they don't already exist
    // Checks first to avoid duplicates
    suspend fun addPredefinedMoviesIfNotExists() {
        // Check if any of the predefined movies already exists
        val existingMovie = movieDao.getMovieByTitle("The Shawshank Redemption")

        // Only add the movies if they don't exist
        if (existingMovie == null) {
            addPredefinedMovies()
        }
    }

    // Add predefined movies to the database
    // Contains hard-coded movie data for initial setup
    suspend fun addPredefinedMovies() {
        val movies = listOf(
            Movie(
                title = "The Shawshank Redemption",
                year = "1994",
                rated = "R",
                released = "14 Oct 1994",
                runtime = "142 min",
                genre = "Drama",
                director = "Frank Darabont",
                writer = "Stephen King, Frank Darabont",
                actors = "Tim Robbins, Morgan Freeman, Bob Gunton",
                plot = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
            ),
            Movie(
                title = "The Godfather",
                year = "1972",
                rated = "R",
                released = "24 Mar 1972",
                runtime = "175 min",
                genre = "Crime, Drama",
                director = "Francis Ford Coppola",
                writer = "Mario Puzo, Francis Ford Coppola",
                actors = "Marlon Brando, Al Pacino, James Caan",
                plot = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son."
            ),
            Movie(
                title = "The Dark Knight",
                year = "2008",
                rated = "PG-13",
                released = "18 Jul 2008",
                runtime = "152 min",
                genre = "Action, Crime, Drama",
                director = "Christopher Nolan",
                writer = "Jonathan Nolan, Christopher Nolan",
                actors = "Christian Bale, Heath Ledger, Aaron Eckhart",
                plot = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice."
            ),
            Movie(
                title = "Pulp Fiction",
                year = "1994",
                rated = "R",
                released = "14 Oct 1994",
                runtime = "154 min",
                genre = "Crime, Drama",
                director = "Quentin Tarantino",
                writer = "Quentin Tarantino, Roger Avary",
                actors = "John Travolta, Uma Thurman, Samuel L. Jackson",
                plot = "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption."
            ),
            Movie(
                title = "Fight Club",
                year = "1999",
                rated = "R",
                released = "15 Oct 1999",
                runtime = "139 min",
                genre = "Drama",
                director = "David Fincher",
                writer = "Chuck Palahniuk, Jim Uhls",
                actors = "Brad Pitt, Edward Norton, Meat Loaf",
                plot = "An insomniac office worker and a devil-may-care soapmaker form an underground fight club that evolves into something much, much more."
            )
        )

        // Insert each movie and its actors
        for (movie in movies) {
            // Use the method that checks for duplicates
            insertMovieWithActors(movie)
        }
    }
    // Add this method to MovieRepository.kt
    suspend fun removeDuplicateMovies() {
        // Get all movies
        val allMovies = movieDao.getAllMoviesSync()

        // Group movies by title (assuming title is what makes a movie unique)
        val moviesGroupedByTitle = allMovies.groupBy { it.title }

        // For each group of movies with the same title, keep only the first one
        var duplicatesRemoved = 0
        moviesGroupedByTitle.forEach { (title, movies) ->
            if (movies.size > 1) {
                // Keep the first movie, delete the rest
                val moviesToDelete = movies.drop(1)
                moviesToDelete.forEach { movie ->
                    // Delete actors for this movie first
                    actorDao.deleteActorsByMovieId(movie.id)
                    // Then delete the movie
                    movieDao.deleteMovie(movie.id)
                    duplicatesRemoved++
                }
            }
        }

        println("Removed $duplicatesRemoved duplicate movies")
    }

}