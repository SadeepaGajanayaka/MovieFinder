package com.example.moviefinder.data.local.entity

// Importing Room database annotations
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.moviefinder.data.local.Converters

// Entity annotation marks this data class as a database table
// tableName specifies the name of the table in the database
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true)  // Primary key with auto-increment
    val id: Long = 0,                 // Default value 0 means new record
    val title: String,                // Movie title
    val year: String,                 // Release year
    val rated: String,                // Rating (PG, R, etc.)
    val released: String,             // Release date
    val runtime: String,              // Movie duration
    val genre: String,                // Movie genre(s)
    val director: String,             // Director name(s)
    val writer: String,               // Writer name(s)
    val actors: String,               // Comma-separated actor names
    val plot: String,                 // Movie plot summary
    val language: String = "",        // Movie language(s)
    val country: String = "",         // Production country
    val awards: String = "",          // Awards received
    val poster: String = "",          // URL to poster image
    val imdbRating: String = "",      // IMDB rating
    val imdbVotes: String = "",       // Number of IMDB votes
    val imdbID: String = "",          // IMDB unique identifier
    val type: String = "",            // Media type (movie, series, etc.)
)