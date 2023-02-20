package com.example.data.storages

import com.example.data.storages.apistorage.entities.series.SeriesResponse
import retrofit2.Response

interface ApiSeriesStorage {
    suspend fun getSeriesById(id: Int): Response<com.example.data.storages.apistorage.entities.series.SeriesResponse>
}