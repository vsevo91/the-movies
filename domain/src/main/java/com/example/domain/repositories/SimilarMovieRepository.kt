package com.example.domain.repositories

import com.example.domain.entities.movie.MovieSimilar

interface SimilarMovieRepository {
    suspend fun getSimilarMovieById(movieId: Int): List<MovieSimilar>
}