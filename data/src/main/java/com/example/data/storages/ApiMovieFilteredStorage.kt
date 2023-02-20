package com.example.data.storages

import com.example.data.storages.apistorage.entities.movie.MovieByFiltersListResponse
import retrofit2.Response

interface ApiMovieFilteredStorage {
    suspend fun getMovieFilteredPaged(
        countries: Array<Int>?,
        genres: Array<Int>?,
        order: String,
        type: String,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int,
        yearTo: Int,
        keyword: String,
        page: Int
    ): Response<MovieByFiltersListResponse>
}