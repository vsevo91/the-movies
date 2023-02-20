package com.example.domain.usecases

import com.example.domain.entities.movie.MovieTopListPaged
import com.example.domain.repositories.MovieTopRepository
import javax.inject.Inject

class GetMovieTopListByTypeUseCase @Inject constructor(
    private val movieTopRepository: MovieTopRepository
) {
    suspend fun execute(type: String, page: Int): MovieTopListPaged {
        return movieTopRepository.getMovieTopListByTypeAndPage(type, page)
    }
}