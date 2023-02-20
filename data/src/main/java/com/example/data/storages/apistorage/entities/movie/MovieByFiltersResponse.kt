package com.example.data.storages.apistorage.entities.movie

data class MovieByFiltersResponse(
    val kinopoiskId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val posterUrlPreview: String,
    val posterUrl: String,
    val genres: List<com.example.data.storages.apistorage.entities.movie.GenreResponse>,
    val ratingKinopoisk: Double?,
    val nameOriginal: String?,
    val countries: List<com.example.data.storages.apistorage.entities.movie.CountryResponse>,
    val year: Int?,
    val type: String
)