package com.example.domain.usecases

import com.example.domain.entities.movie.MoviePremiere
import com.example.domain.repositories.PremiereRepository
import javax.inject.Inject

class GetPremieresListUseCase @Inject constructor(
    private val premiereRepository: PremiereRepository
) {
    suspend fun execute(year: Int, month: String): List<MoviePremiere> {
        return premiereRepository.getPremieresListByYearAndMonth(year, month)
    }
}