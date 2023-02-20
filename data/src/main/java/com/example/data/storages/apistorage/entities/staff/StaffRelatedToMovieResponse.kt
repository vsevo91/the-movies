package com.example.data.storages.apistorage.entities.staff

data class StaffRelatedToMovieResponse(
    val staffId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val description: String?,
    val posterUrl: String,
    val professionText: String,
    val professionKey: String
)