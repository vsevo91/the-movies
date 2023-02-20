package com.example.data.storages

import com.example.data.storages.apistorage.entities.movie.MovieTopListResponse
import retrofit2.Response

interface ApiMovieTopStorage {
    suspend fun getMovieTopByIdPaged(type: String, page: Int): Response<com.example.data.storages.apistorage.entities.movie.MovieTopListResponse>
}