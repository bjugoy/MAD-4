package com.example.movieappmad24.viewmodels

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.movieappmad24.models.Movie
import com.example.movieappmad24.models.getMovies
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

// Inherit from ViewModel class
class MoviesViewModel : ViewModel() {
    private val _movies = mutableStateListOf<Movie>()

    init {
        // Initialize movies list
        _movies.addAll(getMovies())
    }

    // Expose a copy of the movies list
    val movies: List<Movie>
        get() = _movies.toList()

    // Expose favorite movies list
    val favoriteMovies: List<Movie>
        get() = _movies.filter { it.isFavorite }

    // Toggle the favorite status of a movie
    fun toggleFavoriteMovie(movieId: String) {
        val movie = _movies.find { it.id == movieId }
        movie?.isFavorite = movie?.isFavorite?.not() ?: false
    }
}
