package com.example.data.storages.apistorage.moviesstorage

import com.example.data.storages.ApiMovieTopStorage
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.entities.movie.MovieTopListResponse
import retrofit2.Response
import javax.inject.Inject

class ApiMovieTopStorageImpl @Inject constructor(
    private val kinopoiskService: KinopoiskService
) : ApiMovieTopStorage {
    override suspend fun getMovieTopByIdPaged(
        type: String,
        page: Int
    ): Response<com.example.data.storages.apistorage.entities.movie.MovieTopListResponse> {
        return kinopoiskService.getMovieTopByCategoryPaged(type, page)
    }
}