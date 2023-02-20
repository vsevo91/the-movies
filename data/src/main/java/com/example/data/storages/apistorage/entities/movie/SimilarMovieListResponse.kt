package com.example.data.storages.apistorage.entities.movie

data class SimilarMovieListResponse(
    val total: Int,
    val items: List<com.example.data.storages.apistorage.entities.movie.SimilarMovieResponse>
)