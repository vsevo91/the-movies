package com.example.movies.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.GetIfShowOnboardingScreenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getIfShowOnboardingScreenUseCase: GetIfShowOnboardingScreenUseCase
) : ViewModel() {

    private val _ifShow = MutableSharedFlow<Boolean>()
    val ifShow: SharedFlow<Boolean> get() = _ifShow
    private var appIsRunning = false

    init {
        delayForSplashScreen()
    }

    var isReady = false

    private fun delayForSplashScreen() {
        viewModelScope.launch {
            delay(1000)
            isReady = true
        }
    }

    fun ifShowOnboardingScreen() {
        viewModelScope.launch {
            val value = getIfShowOnboardingScreenUseCase.execute()
            _ifShow.emit(value)
        }
    }

    fun setAppIsRunning(isRunning: Boolean) {
        appIsRunning = isRunning
    }

    fun checkIfAppIsRunning(): Boolean {
        return appIsRunning
    }
}