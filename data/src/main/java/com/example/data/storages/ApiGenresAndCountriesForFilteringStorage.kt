package com.example.data.storages

import com.example.data.storages.apistorage.entities.filtering.GenresAndCountriesForFilteringResponse
import retrofit2.Response

interface ApiGenresAndCountriesForFilteringStorage {
    suspend fun getGenresAndCountriesForFiltering(): Response<com.example.data.storages.apistorage.entities.filtering.GenresAndCountriesForFilteringResponse>
}