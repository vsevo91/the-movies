package com.example.data.storages.apistorage.entities.movie

data class PremiereListResponse(
    val total: Int,
    val items: List<com.example.data.storages.apistorage.entities.movie.PremiereResponse>
)