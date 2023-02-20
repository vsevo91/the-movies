package com.example.movies.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.entities.staff.StaffFullInfo
import com.example.domain.usecases.GetMovieFullInfoByIdUseCase
import com.example.domain.usecases.GetStaffFullInfoByStaffIdUseCase
import com.example.domain.usecases.GetUserCollectionsUseCase
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.WATCHED_COLLECTION_ID
import com.example.movies.presentation.extensions.makeQuerySafely
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val getStaffFullInfoByStaffIdUseCase: GetStaffFullInfoByStaffIdUseCase,
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

    private val _staff = MutableLiveData<StaffFullInfo>()
    val staffLiveData: LiveData<StaffFullInfo> get() = _staff

    fun getStaffFullInfoByStaffId(staffId: Int) {
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val staff = getStaffFullInfoByStaffIdUseCase.execute(staffId)
                _staff.value = staff
            }
        }
    }

    fun getMovieFullInfoSource(movieId: Int): Flow<MovieFullInfo> {
        val movieChannel = Channel<MovieFullInfo>()
        val movieFlow = movieChannel.receiveAsFlow()
        viewModelScope.launch {
            makeQuerySafely(
                _connectionErrorState,
                _otherErrorState,
                onError = { movieChannel.cancel() }
            ) {
                Log.d(APPLICATION_TAG, "$movieId: SEND")
                val movieFullInfo = getMovieFullInfoByIdUseCase.execute(movieId)
                movieChannel.send(movieFullInfo!!)
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