package com.example.moviefinder.data.local.entity

// Importing necessary Room database annotations
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Entity annotation defines this as a database table
// tableName specifies the name in the database
// indices creates an index on movieId for faster lookups
// foreignKeys establishes relationship with Movie entity
@Entity(
    tableName = "actors",
    indices = [Index("movieId")],
    foreignKeys = [
        ForeignKey(
            entity = Movie::class,            // Parent entity is Movie
            parentColumns = ["id"],           // Parent key
            childColumns = ["movieId"],       // Foreign key in this entity
            onDelete = ForeignKey.CASCADE     // If movie deleted, delete all its actors
        )
    ]
)
// Data class for Actor entity with properties that map to table columns
data class Actor(
    @PrimaryKey(autoGenerate = true)    // Primary key with auto-increment
    val id: Long = 0,                   // Default value 0 means no ID assigned yet
    val name: String,                   // Actor's name
    val movieId: Long                   // Foreign key to link with Movie table
)