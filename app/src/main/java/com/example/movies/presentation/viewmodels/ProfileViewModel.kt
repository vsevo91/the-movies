package com.example.movies.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.domain.entities.UserCollection
import com.example.domain.usecases.AddUserCollectionUseCase
import com.example.domain.usecases.DeleteUserCollectionUseCase
import com.example.domain.usecases.GetUserCollectionsUseCase
import com.example.domain.utilities.APPLICATION_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserCollectionsUseCase: GetUserCollectionsUseCase,
    private val addUserCollectionUseCase: AddUserCollectionUseCase,
    private val deleteUserCollectionUseCase: DeleteUserCollectionUseCase
) : ViewModel() {

    val collectionsFlow = getUserCollectionsUseCase.execute().asLiveData()

    fun addCollection(collection: UserCollection) {
        viewModelScope.launch {
            addUserCollectionUseCase.execute(collection)
        }
    }

    fun deleteCollection(collection: UserCollection) {
        viewModelScope.launch {
            val deleteResult = deleteUserCollectionUseCase.execute(collection)
            Log.d(APPLICATION_TAG, "Item was deleted: $deleteResult")
        }
    }
}