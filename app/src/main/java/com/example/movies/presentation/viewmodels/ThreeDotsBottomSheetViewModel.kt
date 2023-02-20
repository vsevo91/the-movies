package com.example.movies.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.domain.entities.UserCollection
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.usecases.*
import com.example.domain.utilities.APPLICATION_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThreeDotsBottomSheetViewModel @Inject constructor(
    getUserCollectionsUseCase: GetUserCollectionsUseCase,
    private val addUserCollectionUseCase: AddUserCollectionUseCase,
    private val addMovieToUserCollectionUseCase: AddMovieToUserCollectionUseCase,
    private val deleteMovieFromUserCollectionUseCase: DeleteMovieFromUserCollectionUseCase,
    private val deleteUserCollectionUseCase: DeleteUserCollectionUseCase
) : ViewModel() {

    val collectionsLiveData = getUserCollectionsUseCase.execute().asLiveData()

    fun addCollection(collection: UserCollection) {
        viewModelScope.launch {
            addUserCollectionUseCase.execute(collection)
        }
    }

    fun addOrDeleteMovieToCollection(movie: MovieFullInfo, collectionId: Int) {
        viewModelScope.launch {
            if (collectionsLiveData.value!!.first { it.id == collectionId }.movies.any { it.id == movie.id }) {
                deleteMovieFromUserCollectionUseCase.execute(movie.id, collectionId)
            } else {
                addMovieToUserCollectionUseCase.execute(movie, collectionId)
            }
        }
    }

    fun deleteCollection(collection: UserCollection) {
        viewModelScope.launch {
            val deleteResult = deleteUserCollectionUseCase.execute(collection)
            Log.d(APPLICATION_TAG, "Item was deleted: $deleteResult")
        }
    }
}