package com.example.data.storages.apistorage.entities.gallery

data class GalleryImageListResponse(
    val total: Int,
    val totalPages: Int,
    val items: List<com.example.data.storages.apistorage.entities.gallery.GalleryImageResponse>
)