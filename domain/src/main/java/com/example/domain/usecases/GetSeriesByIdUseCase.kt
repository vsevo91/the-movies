package com.example.domain.usecases

import com.example.domain.entities.series.Series
import com.example.domain.repositories.SeriesRepository
import javax.inject.Inject

class GetSeriesByIdUseCase @Inject constructor(
    private val seriesRepository: SeriesRepository
) {
    suspend fun execute(id: Int): Series {
        return seriesRepository.getSeriesById(id)
    }
}