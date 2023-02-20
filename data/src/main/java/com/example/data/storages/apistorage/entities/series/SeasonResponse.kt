package com.example.data.storages.apistorage.entities.series


data class SeasonResponse(
    val number: Int,
    val episodes: List<com.example.data.storages.apistorage.entities.series.EpisodeResponse>
)