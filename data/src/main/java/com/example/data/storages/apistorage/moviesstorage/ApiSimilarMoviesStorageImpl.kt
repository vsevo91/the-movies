package com.example.data.storages.apistorage.moviesstorage

import com.example.data.storages.ApiSimilarMoviesStorage
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.entities.movie.SimilarMovieListResponse
import retrofit2.Response

class ApiSimilarMoviesStorageImpl(
    private val kinopoiskService: KinopoiskService
) : ApiSimilarMoviesStorage {
    override suspend fun getSimilarMoviesById(movieId: Int): Response<com.example.data.storages.apistorage.entities.movie.SimilarMovieListResponse> {
        return kinopoiskService.getSimilarMoviesById(movieId)
    }
}