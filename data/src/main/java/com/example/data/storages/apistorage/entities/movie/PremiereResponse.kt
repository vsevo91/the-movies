package com.example.data.storages.apistorage.entities.movie

data class PremiereResponse(
    val kinopoiskId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val year: Int,
    val posterUrl: String,
    val posterUrlPreview: String,
    val countries: List<com.example.data.storages.apistorage.entities.movie.CountryResponse>,
    val genres: List<com.example.data.storages.apistorage.entities.movie.GenreResponse>,
    val duration: Int?,
    val premiereRu: String
)