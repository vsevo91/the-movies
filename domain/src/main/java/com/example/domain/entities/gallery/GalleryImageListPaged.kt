package com.example.domain.entities.gallery

data class GalleryImageListPaged(
    val total: Int,
    val totalPages: Int,
    val items: List<GalleryImage>
)