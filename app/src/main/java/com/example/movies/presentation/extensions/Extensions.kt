package com.example.movies.presentation.extensions

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.R
import com.example.movies.presentation.utilities.ErrorDialogManager
import java.net.UnknownHostException

fun Fragment.doIfError(isConnectionProblem: Boolean, onRefreshButtonClick: () -> Unit) {
    val message = if (isConnectionProblem) {
        getString(R.string.check_internet_connection)
    } else {
        getString(R.string.something_went_wrong)
    }
    val fm = this.requireActivity().supportFragmentManager

    ErrorDialogManager.showDialog(fm,message, onRefreshButtonClick)
}

suspend fun ViewModel.makeQuerySafely(
    liveDataIfConnectionError: MutableLiveData<Boolean>,
    liveDataIfOtherError: MutableLiveData<Boolean>,
    onError: (() -> Unit)? = null,
    query: suspend () -> Unit
) {
    try {
        query()
    } catch (e: Exception) {
        onError?.invoke()
        if (e is UnknownHostException) {
            liveDataIfConnectionError.value = true
        } else {
            liveDataIfOtherError.value = true
        }
        val message = buildString {
            appendLine("Error occurred in \"${this@makeQuerySafely.javaClass.name}\"")
            appendLine("Error string: ${e.stackTraceToString()}")
        }
        Log.d(APPLICATION_TAG, message)
    }
}