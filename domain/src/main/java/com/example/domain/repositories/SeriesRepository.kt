package com.example.domain.repositories

import com.example.domain.entities.series.Series

interface SeriesRepository {
    suspend fun getSeriesById(id: Int): Series
}