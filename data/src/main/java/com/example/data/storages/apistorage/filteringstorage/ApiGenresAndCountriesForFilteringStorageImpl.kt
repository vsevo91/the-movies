package com.example.data.storages.apistorage.filteringstorage

import com.example.data.storages.ApiGenresAndCountriesForFilteringStorage
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.entities.filtering.GenresAndCountriesForFilteringResponse
import retrofit2.Response
import javax.inject.Inject

class ApiGenresAndCountriesForFilteringStorageImpl @Inject constructor(
    private val kinopoiskService: KinopoiskService
) : ApiGenresAndCountriesForFilteringStorage {
    override suspend fun getGenresAndCountriesForFiltering(): Response<com.example.data.storages.apistorage.entities.filtering.GenresAndCountriesForFilteringResponse> {
        return kinopoiskService.getGenresAndCountriesForFiltering()
    }
}