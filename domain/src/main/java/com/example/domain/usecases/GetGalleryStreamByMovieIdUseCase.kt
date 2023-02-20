package com.example.domain.usecases

import androidx.paging.PagingData
import com.example.domain.entities.gallery.GalleryImage
import com.example.domain.repositories.GalleryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetGalleryStreamByMovieIdUseCase @Inject constructor(
    private val galleryRepository: GalleryRepository
) {
    fun execute(movieId: Int, type: String): Flow<PagingData<GalleryImage>> {
        return galleryRepository.getGalleryByIdStream(movieId, type)
    }
}