package com.example.data.storages.apistorage.entities.movie

data class MovieTopResponse(
    val filmId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val year: String?,
    val filmLength: String?,
    val countries: List<com.example.data.storages.apistorage.entities.movie.CountryResponse>,
    val genres: List<com.example.data.storages.apistorage.entities.movie.GenreResponse>,
    val rating: String?,
    val ratingVoteCount: Int?,
    val posterUrl: String,
    val posterUrlPreview: String
)
