package com.example.domain.usecases

import com.example.domain.entities.movie.MovieSimilar
import com.example.domain.repositories.SimilarMovieRepository
import javax.inject.Inject

class GetSimilarMoviesByMovieIdUseCase @Inject constructor(
    private val similarMovieRepository: SimilarMovieRepository
) {
    suspend fun execute(movieId: Int): List<MovieSimilar> {
        return similarMovieRepository.getSimilarMovieById(movieId)
    }
}