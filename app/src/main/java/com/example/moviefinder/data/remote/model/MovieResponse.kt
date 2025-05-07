package com.example.moviefinder.data.remote.model

// Import SerializedName annotation from Gson for JSON mapping
import com.google.gson.annotations.SerializedName

// Data class for movie details from OMDB API
// Maps JSON response to Kotlin object
data class MovieResponse(
    @SerializedName("Title") val title: String,        // Movie title from API
    @SerializedName("Year") val year: String,          // Release year
    @SerializedName("Rated") val rated: String,        // Content rating (PG, R, etc.)
    @SerializedName("Released") val released: String,  // Release date
    @SerializedName("Runtime") val runtime: String,    // Movie duration
    @SerializedName("Genre") val genre: String,        // Movie genre(s)
    @SerializedName("Director") val director: String,  // Director name(s)
    @SerializedName("Writer") val writer: String,      // Writer name(s)
    @SerializedName("Actors") val actors: String,      // Comma-separated actor names
    @SerializedName("Plot") val plot: String,          // Plot summary
    @SerializedName("Language") val language: String,  // Movie language(s)
    @SerializedName("Country") val country: String,    // Production country
    @SerializedName("Awards") val awards: String,      // Awards information
    @SerializedName("Poster") val poster: String,      // URL to poster image
    @SerializedName("Ratings") val ratings: List<Rating>, // Ratings from different sources
    @SerializedName("Metascore") val metascore: String,   // Metascore rating
    @SerializedName("imdbRating") val imdbRating: String, // IMDB rating
    @SerializedName("imdbVotes") val imdbVotes: String,   // Number of IMDB votes
    @SerializedName("imdbID") val imdbID: String,         // IMDB unique identifier
    @SerializedName("Type") val type: String,             // Media type (movie, series, etc.)
    @SerializedName("Response") val response: String      // API response status
)

// Data class for movie ratings from different sources
data class Rating(
    @SerializedName("Source") val source: String,  // Rating source (IMDB, Rotten Tomatoes, etc.)
    @SerializedName("Value") val value: String     // Rating value
)

// Data class for search results response
// Used when searching for movies by title
data class SearchResponse(
    @SerializedName("Search") val search: List<SearchResult>,  // List of movie search results
    @SerializedName("totalResults") val totalResults: String,  // Total number of results
    @SerializedName("Response") val response: String           // API response status
)

// Data class for individual search result
// Contains basic movie information
data class SearchResult(
    @SerializedName("Title") val title: String,    // Movie title
    @SerializedName("Year") val year: String,      // Release year
    @SerializedName("imdbID") val imdbID: String,  // IMDB unique identifier
    @SerializedName("Type") val type: String,      // Media type (movie, series, etc.)
    @SerializedName("Poster") val poster: String   // URL to poster image
)