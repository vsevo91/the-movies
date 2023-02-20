package com.example.domain.usecases

import com.example.domain.entities.gallery.GalleryImageListPaged
import com.example.domain.repositories.GalleryRepository
import javax.inject.Inject


class GetGalleryByMovieIdUseCase @Inject constructor(
    private val galleryRepository: GalleryRepository
) {
    suspend fun execute(movieId: Int, page: Int, type: String): GalleryImageListPaged {
        return galleryRepository.getGalleryByIdAndByPage(movieId, page, type)
    }
}