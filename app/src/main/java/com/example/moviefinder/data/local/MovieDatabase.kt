package com.example.moviefinder.data.local

// Importing Android context for database creation
import android.content.Context
// Importing Room database annotations and classes
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
// Importing entity classes for our database
import com.example.moviefinder.data.local.entity.Actor
import com.example.moviefinder.data.local.entity.Movie

// Database annotation defines database configuration
// entities lists all tables in the database
// version specifies the schema version (important for migrations)
// exportSchema is for schema version history (disabled here)
@Database(entities = [Movie::class, Actor::class], version = 1, exportSchema = false)
// TypeConverters annotation specifies converters for complex data types
@TypeConverters(Converters::class)
abstract class MovieDatabase : RoomDatabase() {

    // Abstract methods that return DAO interfaces
    // Room will generate implementations at compile time
    abstract fun movieDao(): MovieDao
    abstract fun actorDao(): ActorDao

    // Companion object for database creation and access
    // Implements Singleton pattern to avoid multiple database instances
    companion object {
        // Volatile ensures the variable is always up to date across threads
        @Volatile
        private var INSTANCE: MovieDatabase? = null

        // Function to get database instance
        // Creates a new instance if none exists
        fun getDatabase(context: Context): MovieDatabase {
            // Return existing instance if available
            return INSTANCE ?: synchronized(this) {
                // If no instance exists, create a new one
                // synchronized block ensures thread safety
                val instance = Room.databaseBuilder(
                    context.applicationContext,  // Application context to avoid memory leaks
                    MovieDatabase::class.java,   // Database class
                    "movie_database"             // Database file name
                )
                    .fallbackToDestructiveMigration()  // Recreate database if schema version changes
                    .build()
                // Save the instance to avoid creating another one
                INSTANCE = instance
                // Return the new instance
                instance
            }
        }
    }
}