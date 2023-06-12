package com.example.movies.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.GetIfShowOnboardingScreenUseCase
import com.example.domain.utilities.APPLICATION_TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getIfShowOnboardingScreenUseCase: GetIfShowOnboardingScreenUseCase
) : ViewModel() {

    private val _ifShow = MutableLiveData<Boolean>()
    val ifShow: LiveData<Boolean> get() = _ifShow
    private var appIsRunning = false

    init {
      delayForSplashScreen()
    }

    var isReady = false

    private fun delayForSplashScreen(){
        viewModelScope.launch {
            delay(1000)
            isReady = true
        }
    }

    fun ifShowOnboardingScreen() {
        viewModelScope.launch {
            val value = getIfShowOnboardingScreenUseCase.execute()
            Log.d(APPLICATION_TAG, "Info from activity vm. If show? -> $value")
            _ifShow.value = value
        }
    }

    fun setAppIsRunning(isRunning:Boolean){
        appIsRunning = isRunning
    }

    fun checkIfAppIsRunning():Boolean{
        return appIsRunning
    }
}