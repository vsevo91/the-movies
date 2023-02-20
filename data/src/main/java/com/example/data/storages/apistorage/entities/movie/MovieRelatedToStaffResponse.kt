package com.example.data.storages.apistorage.entities.movie

data class MovieRelatedToStaffResponse(
    val filmId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val rating: String?,
    val general: Boolean,
    val description: String?,
    val professionKey: String
)