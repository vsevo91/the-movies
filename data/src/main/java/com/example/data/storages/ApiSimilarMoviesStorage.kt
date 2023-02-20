package com.example.data.storages

import com.example.data.storages.apistorage.entities.movie.SimilarMovieListResponse
import retrofit2.Response

interface ApiSimilarMoviesStorage {
    suspend fun getSimilarMoviesById(movieId: Int): Response<com.example.data.storages.apistorage.entities.movie.SimilarMovieListResponse>
}