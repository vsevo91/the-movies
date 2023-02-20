package com.example.movies.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.usecases.GetMovieFullInfoByIdUseCase
import com.example.domain.usecases.GetUserCollectionsUseCase
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.WATCHED_COLLECTION_ID
import com.example.movies.presentation.extensions.makeQuerySafely
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffFilmographyViewModel @Inject constructor(
    private val getMovieFullInfoByIdUseCase: GetMovieFullInfoByIdUseCase,
    getUserCollectionsUseCase: GetUserCollectionsUseCase
) : ViewModel() {

    private val _connectionErrorState = MutableLiveData(false)
    val connectionErrorState: LiveData<Boolean> = _connectionErrorState

    private val _otherErrorState = MutableLiveData(false)
    val otherErrorState: LiveData<Boolean> = _otherErrorState

    val watchedMovies = getUserCollectionsUseCase.execute().map { userCollections ->
        userCollections.firstOrNull { userCollection ->
            userCollection.id == WATCHED_COLLECTION_ID
        }?.movies
    }.asLiveData(viewModelScope.coroutineContext)

    fun getMovieFullInfoByMovieId(movieId: Int): Flow<MovieFullInfo> {
        val movieChannel = Channel<MovieFullInfo>()
        val movieFlow = movieChannel.receiveAsFlow()
        viewModelScope.launch {
            makeQuerySafely(
                _connectionErrorState,
                _otherErrorState,
                onError = { movieChannel.cancel() }) {
                Log.d(APPLICATION_TAG, "$movieId: SEND")
                val movie = getMovieFullInfoByIdUseCase.execute(movieId)
                movieChannel.send(movie!!)
                movieChannel.cancel()
            }
        }
        return movieFlow
    }

    fun clearErrorState(){
        _connectionErrorState.value = false
        _otherErrorState.value = false
    }
}