package com.example.moviefinder.data.remote

// Importing API response data classes
import com.example.moviefinder.data.remote.model.MovieResponse
import com.example.moviefinder.data.remote.model.SearchResponse
// Importing Retrofit for API communication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Interface for OMDB API service
// Retrofit will generate implementation at compile time
interface OmdbApiService {
    // GET request to root endpoint ("/")
    // For retrieving a specific movie by title
    @GET("/")
    suspend fun getMovieByTitle(
        @Query("t") title: String,          // Title parameter for API
        @Query("apikey") apiKey: String     // API key for authentication
    ): MovieResponse                         // Returns movie details

    // GET request to root endpoint ("/")
    // For searching movies by search term
    @GET("/")
    suspend fun searchMovies(
        @Query("s") searchTerm: String,     // Search term parameter
        @Query("apikey") apiKey: String,    // API key for authentication
        @Query("page") page: Int = 1        // Optional page number, default is 1
    ): SearchResponse                        // Returns search results

    // Companion object for creating and accessing service instance
    // Implements Singleton pattern
    companion object {
        // Base URL for OMDB API
        private const val BASE_URL = "https://www.omdbapi.com/"
        // Static instance of the service
        private var apiService: OmdbApiService? = null

        // API key for OMDB API
        // Hard-coded for simplicity, but in production should be in secure storage
        const val API_KEY = "2f1654d6"

        // Function to get service instance
        // Creates new instance if none exists
        fun getInstance(): OmdbApiService {
            if (apiService == null) {
                // Create Retrofit instance with base URL and JSON converter
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                // Create service implementation
                apiService = retrofit.create(OmdbApiService::class.java)
            }
            // Return existing or new instance
            return apiService!!
        }
    }
}