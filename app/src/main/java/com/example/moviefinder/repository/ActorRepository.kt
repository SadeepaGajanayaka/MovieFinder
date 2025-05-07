package com.example.moviefinder.repository

// Importing the ActorDao interface for database operations
import com.example.moviefinder.data.local.ActorDao
// Importing the Actor entity
import com.example.moviefinder.data.local.entity.Actor
// Importing Flow for reactive data streams
import kotlinx.coroutines.flow.Flow

// Repository class for Actor-related operations
// Acts as a mediator between data sources and the rest of the app
class ActorRepository(private val actorDao: ActorDao) {

    // Inserts a single actor into the database
    // Returns the ID of the inserted actor
    suspend fun insertActor(actor: Actor): Long {
        return actorDao.insertActor(actor)
    }

    // Inserts multiple actors into the database at once
    // Useful for batch operations
    suspend fun insertActors(actors: List<Actor>) {
        actorDao.insertActors(actors)
    }

    // Gets all actors for a specific movie as a Flow
    // Flow allows observing data changes over time
    fun getActorsByMovieId(movieId: Long): Flow<List<Actor>> {
        return actorDao.getActorsByMovieId(movieId)
    }

    // Searches for actors by name
    // Returns a Flow of matching actors
    fun getActorsByName(name: String): Flow<List<Actor>> {
        return actorDao.getActorsByName(name)
    }

    // Deletes all actors associated with a specific movie
    // Used when deleting a movie or cleaning up
    suspend fun deleteActorsByMovieId(movieId: Long) {
        actorDao.deleteActorsByMovieId(movieId)
    }
}