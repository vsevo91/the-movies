package com.example.data.storages.apistorage.moviesstorage

import com.example.data.storages.ApiMovieFilteredStorage
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.entities.movie.MovieByFiltersListResponse
import retrofit2.Response
import javax.inject.Inject

class ApiMovieFilteredStorageImpl @Inject constructor(
    private val kinopoiskService: KinopoiskService
) : ApiMovieFilteredStorage {
    override suspend fun getMovieFilteredPaged(
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
    ): Response<com.example.data.storages.apistorage.entities.movie.MovieByFiltersListResponse> {
        return kinopoiskService.getMovieListFilteredPaged(
            countries,
            genres,
            order,
            type,
            ratingFrom,
            ratingTo,
            yearFrom,
            yearTo,
            keyword,
            page
        )
    }
}