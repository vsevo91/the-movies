package com.example.data.storages.apistorage.entities.staff

import com.example.data.storages.apistorage.entities.movie.MovieRelatedToStaffResponse

data class StaffFullInfoResponse(
    val personId: Int,
    val webUrl: String?,
    val nameRu: String?,
    val nameEn: String?,
    val sex: String?,
    val posterUrl: String,
    val growth: String?,
    val birthday: String?,
    val death: String?,
    val age: Int?,
    val birthplace: String?,
    val deathplace: String?,
    val hasAwards: Int?,
    val profession: String?,
    val facts: List<String>,
    val films: List<com.example.data.storages.apistorage.entities.movie.MovieRelatedToStaffResponse>
)