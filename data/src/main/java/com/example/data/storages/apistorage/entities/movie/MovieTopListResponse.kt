package com.example.data.storages.apistorage.entities.movie

data class MovieTopListResponse(
    val pagesCount: Int,
    val films: List<com.example.data.storages.apistorage.entities.movie.MovieTopResponse>
)
