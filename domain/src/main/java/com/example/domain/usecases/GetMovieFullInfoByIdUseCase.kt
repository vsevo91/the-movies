package com.example.domain.usecases

import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.repositories.MovieFullInfoRepository
import javax.inject.Inject

class GetMovieFullInfoByIdUseCase @Inject constructor(
    private val movieFullInfoRepository: MovieFullInfoRepository
) {
    suspend fun execute(id: Int): MovieFullInfo? {
        return movieFullInfoRepository.getMovieById(id)
    }
}