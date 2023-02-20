package com.example.data.storages.apistorage.entities.movie

data class SimilarMovieResponse(
    val filmId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val nameOriginal: String?,
    val posterUrl: String,
    val posterUrlPreview: String,
    val relationType: String
)