package com.example.moviefinder.data.local

// Import Room's TypeConverter annotation for data conversion
import androidx.room.TypeConverter
// Import Gson for JSON serialization/deserialization
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Type converters class for Room database
// Helps Room handle complex data types that can't be stored directly in SQLite
class Converters {
    // Converts a JSON string back to a List<String>
    // Used when reading from the database
    @TypeConverter
    fun fromString(value: String): List<String> {
        // Create a type token for List<String> to help Gson know what to convert to
        val listType = object : TypeToken<List<String>>() {}.type
        // Use Gson to convert the JSON string back to a List<String>
        return Gson().fromJson(value, listType)
    }

    // Converts a List<String> to a JSON string
    // Used when writing to the database
    @TypeConverter
    fun fromList(list: List<String>): String {
        // Convert the List<String> to a JSON string using Gson
        return Gson().toJson(list)
    }
}