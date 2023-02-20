package com.example.movies.presentation.utilities

import android.app.Dialog
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.presentation.fragments.dialogs.ErrorDialogFragment

object ErrorDialogManager {

    private var isDialogShown = false
    private var dialog: Dialog? = null


    fun showDialog(
        fragmentManager: FragmentManager,
        message: String,
        onRefreshButtonClick: () -> Unit
    ) {
        val dialogFragment = ErrorDialogFragment()
        dialogFragment.setMessage(message)
        if (!isDialogShown) {
            isDialogShown = true
            dialogFragment.show(fragmentManager, ErrorDialogFragment.DIALOG_TAG)
            Log.d(APPLICATION_TAG, "Error dialog was opened")
            dialogFragment.setOnShowListener {
                Log.d(APPLICATION_TAG, "Error dialog IS BEING SHOWED")
                dialog = dialogFragment.dialog
                Log.d(APPLICATION_TAG, "Dialog: $dialog")
            }
            dialogFragment.setOnRefreshButtonClickListener {
                onRefreshButtonClick()
                hideDialog()
            }
        }
    }

    private fun hideDialog() {
        dialog?.cancel()
        dialog = null
        isDialogShown = false
    }
}