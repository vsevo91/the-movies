package com.example.data.storages.apistorage.entities.series

data class SeriesResponse(
    val total: Int,
    val items: List<com.example.data.storages.apistorage.entities.series.SeasonResponse>
)