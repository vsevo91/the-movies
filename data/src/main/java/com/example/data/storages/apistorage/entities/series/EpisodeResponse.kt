package com.example.data.storages.apistorage.entities.series

data class EpisodeResponse(
    val seasonNumber: Int,
    val episodeNumber: Int,
    val nameRu: String?,
    val nameEn: String?,
    val releaseDate: String?
)