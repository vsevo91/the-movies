package com.example.movies.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.entities.staff.StaffRelatedToMovie
import com.example.domain.usecases.GetStaffListByMovieIdUseCase
import com.example.movies.presentation.extensions.makeQuerySafely
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffListViewModel @Inject constructor(
    private val getStaffListByMovieIdUseCase: GetStaffListByMovieIdUseCase
): ViewModel() {

    private val _connectionErrorState = MutableLiveData(false)
    val connectionErrorState: LiveData<Boolean> = _connectionErrorState
    private val _otherErrorState = MutableLiveData(false)
    val otherErrorState: LiveData<Boolean> = _otherErrorState
    private val _staffRelatedToMovieLiveData = MutableLiveData<List<StaffRelatedToMovie>>()
    val staffRelatedToMovieLiveData: LiveData<List<StaffRelatedToMovie>> get() = _staffRelatedToMovieLiveData

    fun getStaffByMovieId(movieId: Int) {
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val staffList = getStaffListByMovieIdUseCase.execute(movieId)
                _staffRelatedToMovieLiveData.value = staffList
            }
        }
    }

    fun clearErrorState(){
        _connectionErrorState.value = false
        _otherErrorState.value = false
    }
}