package com.example.data.storages

import com.example.data.storages.apistorage.entities.gallery.GalleryImageListResponse
import retrofit2.Response

interface ApiGalleryStorage {
    suspend fun getGalleryByIdPaged(movieId: Int, page: Int, type: String): Response<com.example.data.storages.apistorage.entities.gallery.GalleryImageListResponse>
}