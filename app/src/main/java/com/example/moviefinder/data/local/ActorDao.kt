package com.example.moviefinder.data.local

// Importing Room database annotations and Actor entity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviefinder.data.local.entity.Actor
import kotlinx.coroutines.flow.Flow

// Data Access Object interface for Actor entity
// Room will generate implementation at compile time
@Dao
interface ActorDao {
    // Insert a single actor and return its ID
    // If there's a conflict (same ID), replace the existing record
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActor(actor: Actor): Long

    // Insert a list of actors
    // Uses suspend function for asynchronous database operation
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActors(actors: List<Actor>)

    // Get all actors for a specific movie as a Flow
    // Flow allows continuous updates when database changes
    @Query("SELECT * FROM actors WHERE movieId = :movieId")
    fun getActorsByMovieId(movieId: Long): Flow<List<Actor>>

    // Get actors by movie ID synchronously (one-time fetch)
    // This is used when we need the data immediately, not as a stream
    @Query("SELECT * FROM actors WHERE movieId = :movieId")
    suspend fun getActorsByMovieIdSync(movieId: Long): List<Actor>

    // Search actors by name, case insensitive
    // COLLATE NOCASE makes the search case-insensitive
    // '%' || :name || '%' creates a SQL LIKE pattern for partial matching
    @Query("SELECT * FROM actors WHERE name LIKE '%' || :name || '%' COLLATE NOCASE")
    fun getActorsByName(name: String): Flow<List<Actor>>

    // Delete all actors associated with a specific movie
    // Used when deleting a movie or cleaning up
    @Query("DELETE FROM actors WHERE movieId = :movieId")
    suspend fun deleteActorsByMovieId(movieId: Long)
}