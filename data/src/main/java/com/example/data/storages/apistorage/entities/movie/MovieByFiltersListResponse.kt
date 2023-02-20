package com.example.data.storages.apistorage.entities.movie

data class MovieByFiltersListResponse(
    val total: Int,
    val totalPages: Int,
    val items: List<com.example.data.storages.apistorage.entities.movie.MovieByFiltersResponse>
)
