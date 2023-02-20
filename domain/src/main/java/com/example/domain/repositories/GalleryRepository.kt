package com.example.domain.repositories

import androidx.paging.PagingData
import com.example.domain.entities.gallery.GalleryImage
import com.example.domain.entities.gallery.GalleryImageListPaged
import kotlinx.coroutines.flow.Flow

interface GalleryRepository {
    suspend fun getGalleryByIdAndByPage(
        movieId: Int,
        page: Int,
        type: String
    ): GalleryImageListPaged

    fun getGalleryByIdStream(
        movieId: Int,
        type: String
    ): Flow<PagingData<GalleryImage>>
}