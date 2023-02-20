package com.example.data.storages

import com.example.data.storages.apistorage.entities.movie.PremiereListResponse
import retrofit2.Response

interface ApiPremieresStorage {
    suspend fun getPremieresList(year: Int, month: String): Response<com.example.data.storages.apistorage.entities.movie.PremiereListResponse>
}