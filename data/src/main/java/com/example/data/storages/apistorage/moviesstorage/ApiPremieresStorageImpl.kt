package com.example.data.storages.apistorage.moviesstorage

import com.example.data.storages.ApiPremieresStorage
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.entities.movie.PremiereListResponse
import retrofit2.Response

class ApiPremieresStorageImpl(
    private val kinopoiskService: KinopoiskService
) : ApiPremieresStorage {

    override suspend fun getPremieresList(year: Int, month: String): Response<com.example.data.storages.apistorage.entities.movie.PremiereListResponse> {
        return kinopoiskService.getPremieres(year, month)
    }
}