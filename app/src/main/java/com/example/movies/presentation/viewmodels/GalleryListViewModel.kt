package com.example.movies.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.entities.gallery.GalleryImage
import com.example.domain.usecases.GetGalleryStreamByMovieIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class GalleryListViewModel @Inject constructor(
    private val getGalleryStreamByMovieIdUseCase: GetGalleryStreamByMovieIdUseCase
) : ViewModel() {

    private val streams = mutableMapOf<String, Flow<PagingData<GalleryImage>>>()

    fun getStream(movieId: Int, type: String): Flow<PagingData<GalleryImage>> {
        if (streams[type] == null) {
            streams[type] = getImagesStreamByType(movieId, type)
        }
        return streams[type]!!
    }

    private fun getImagesStreamByType(movieId: Int, type: String): Flow<PagingData<GalleryImage>> {
        return getGalleryStreamByMovieIdUseCase.execute(movieId, type).cachedIn(viewModelScope)
    }
}