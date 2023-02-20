package com.example.movies.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.gallery.GalleryImage
import com.example.domain.usecases.GetGalleryByMovieIdUseCase
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.presentation.extensions.makeQuerySafely
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryImageFullScreenViewModel @Inject constructor(
    private val getGalleryByMovieIdUseCase: GetGalleryByMovieIdUseCase
) : ViewModel() {

    private val _connectionErrorState = MutableLiveData(false)
    val connectionErrorState: LiveData<Boolean> = _connectionErrorState

    private val _otherErrorState = MutableLiveData(false)
    val otherErrorState: LiveData<Boolean> = _otherErrorState

    fun loadImagesByTypeAndPage(
        movieId: Int,
        page: Int,
        type: String
    ): Flow<List<GalleryImage>> {
        val result = Channel<List<GalleryImage>>()
        val galleryImageFlow = result.receiveAsFlow()
        viewModelScope.launch {
            makeQuerySafely(
                _connectionErrorState,
                _otherErrorState,
            onError = {result.cancel()}){
                val galleryImageList = getGalleryByMovieIdUseCase.execute(movieId, page, type).items
                Log.d(APPLICATION_TAG, "Trying to download additional galleryImageList")
                result.send(galleryImageList)
                this.cancel()
            }
        }
        return galleryImageFlow
    }

    fun clearErrorState(){
        _connectionErrorState.value = false
        _otherErrorState.value = false
    }
}