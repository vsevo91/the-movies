package com.example.data.storages.apistorage.moviesstorage

import com.example.data.storages.ApiMovieFullInfoStorage
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.entities.movie.MovieFullInfoResponse
import retrofit2.Response
import javax.inject.Inject

class ApiMovieFullInfoStorageImpl @Inject constructor(
    private val kinopoiskService: KinopoiskService
) : ApiMovieFullInfoStorage {
    override suspend fun getMovieById(id: Int): Response<com.example.data.storages.apistorage.entities.movie.MovieFullInfoResponse> {
        return kinopoiskService.getMovieById(id)
    }
}