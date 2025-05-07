package com.example.moviefinder.data.local

// Importing Room database annotations and Movie entity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviefinder.data.local.entity.Movie
import kotlinx.coroutines.flow.Flow

// Data Access Object interface for Movie entity
// Room generates the implementation at compile time
@Dao
interface MovieDao {
    // Insert a single movie and return its ID
    // Uses REPLACE strategy to update existing records with same ID
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie): Long

    // Insert multiple movies at once and return their IDs
    // Efficient for batch operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>): List<Long>

    // Get a specific movie by its ID
    // Returns null if no movie found with that ID
    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Long): Movie?

    // Get all movies as a Flow
    // Flow allows observing database changes in real-time
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<Movie>>

    // Find movies by actor name using the actors string field
    // Case-insensitive search using LOWER function
    @Query("SELECT * FROM movies WHERE LOWER(actors) LIKE '%' || LOWER(:actorName) || '%'")
    fun getMoviesByActorName(actorName: String): Flow<List<Movie>>

    // Find a movie by its exact title
    // Returns null if no matching movie found
    @Query("SELECT * FROM movies WHERE title = :title")
    suspend fun getMovieByTitle(title: String): Movie?

    // Find movies with partial title match
    // Case-insensitive search using LOWER function
    @Query("SELECT * FROM movies WHERE LOWER(title) LIKE '%' || LOWER(:titlePart) || '%'")
    fun getMoviesByTitlePart(titlePart: String): Flow<List<Movie>>

    // Advanced actor search that looks in both Movie.actors field and Actor table
    // Uses JOIN to combine data from both tables
    // DISTINCT ensures no duplicate movies in results
    @Query("""
        SELECT DISTINCT m.* FROM movies m 
        LEFT JOIN actors a ON m.id = a.movieId
        WHERE LOWER(m.actors) LIKE '%' || LOWER(:actorName) || '%'
        OR LOWER(a.name) LIKE '%' || LOWER(:actorName) || '%'
    """)
    fun getMoviesByActorNameEnhanced(actorName: String): Flow<List<Movie>>

    // Get all movies synchronously (one-time fetch)
    // Used when we need immediate access to all movies
    @Query("SELECT * FROM movies")
    suspend fun getAllMoviesSync(): List<Movie>

    // Delete a specific movie by ID
    // Actors will be automatically deleted due to CASCADE relationship
    @Query("DELETE FROM movies WHERE id = :movieId")
    suspend fun deleteMovie(movieId: Long)
}