package com.example.movies.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.SubmitEntranceUseCase
import com.example.domain.utilities.APPLICATION_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val submitEntranceUseCase: SubmitEntranceUseCase
): ViewModel() {

    fun submitEntrance(){
        viewModelScope.launch {
            submitEntranceUseCase.execute()
            Log.d(APPLICATION_TAG, "Entrance was committed")
        }
    }
}