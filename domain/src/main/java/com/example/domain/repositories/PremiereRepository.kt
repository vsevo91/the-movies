package com.example.domain.repositories

import com.example.domain.entities.movie.MoviePremiere

interface PremiereRepository {
    suspend fun getPremieresListByYearAndMonth(year: Int, month: String): List<MoviePremiere>
}