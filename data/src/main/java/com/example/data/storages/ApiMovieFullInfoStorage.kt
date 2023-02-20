package com.example.data.storages

import com.example.data.storages.apistorage.entities.movie.MovieFullInfoResponse
import retrofit2.Response

interface ApiMovieFullInfoStorage {
    suspend fun getMovieById(id: Int): Response<com.example.data.storages.apistorage.entities.movie.MovieFullInfoResponse>
}