package com.example.data.storages.apistorage.gallerystorage

import com.example.data.storages.ApiGalleryStorage
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.entities.gallery.GalleryImageListResponse
import retrofit2.Response
import javax.inject.Inject

class ApiGalleryStorageImpl @Inject constructor(
    private val kinopoiskService: KinopoiskService
) : ApiGalleryStorage {
    override suspend fun getGalleryByIdPaged(movieId: Int, page: Int, type: String): Response<com.example.data.storages.apistorage.entities.gallery.GalleryImageListResponse> {
        return kinopoiskService.getGalleryByIdPaged(movieId, page, type)
    }
}