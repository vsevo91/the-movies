package com.example.data.storages.apistorage.seriesstorage

import com.example.data.storages.ApiSeriesStorage
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.entities.series.SeriesResponse
import retrofit2.Response
import javax.inject.Inject

class ApiSeriesStorageImpl @Inject constructor(
    private val kinopoiskService: KinopoiskService
) : ApiSeriesStorage {
    override suspend fun getSeriesById(id: Int): Response<com.example.data.storages.apistorage.entities.series.SeriesResponse> {
        return kinopoiskService.getSeriesById(id)
    }
}