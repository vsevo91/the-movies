package com.example.movies.presentation.viewmodels

import androidx.lifecycle.*
import com.example.domain.entities.UserCollection
import com.example.domain.usecases.GetUserCollectionsUseCase
import com.example.domain.usecases.DeleteMovieFromUserCollectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectionMovieListViewModel @Inject constructor(
    getUserCollectionsUseCase: GetUserCollectionsUseCase,
    private val deleteMovieFromUserCollectionUseCase: DeleteMovieFromUserCollectionUseCase
) : ViewModel() {

    val moviesFromCollection: LiveData<List<UserCollection>> = getUserCollectionsUseCase.execute().asLiveData()

    private val _titleLiveData = MutableLiveData<String>()
    val titleLiveData: LiveData<String> get() = _titleLiveData

    fun deleteMovieFromUserCollection(movieId: Int, collectionId: Int) {
        viewModelScope.launch {
            deleteMovieFromUserCollectionUseCase.execute(movieId, collectionId)
        }
    }
}