package com.example.movies.presentation.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.movies.R
import com.example.movies.databinding.FragmentDialogErrorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ErrorDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogErrorBinding? = null
    private val binding get() = _binding!!
    private var onRefreshButtonClick: (() -> Unit)? = null
    private var onShow: (() -> Unit)? = null
    private var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = requireActivity().layoutInflater
        _binding = FragmentDialogErrorBinding.inflate(inflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.Theme_AppCompat_Dialog_Custom)
            builder.setView(binding.root)
            val dialog = builder.create()
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setOnKeyListener { _, i, _ ->
                if ((i == android.view.KeyEvent.KEYCODE_BACK)) {
                    onRefreshButtonClick?.invoke()
                    true
                } else false
            }
            dialog.setOnShowListener {
                onShow?.invoke()
            }
            binding.refreshButton.setOnClickListener {
                onRefreshButtonClick?.invoke()
            }
            binding.infoText.text = message
            dialog
        } ?: throw IllegalStateException()
    }

    fun setOnRefreshButtonClickListener(onClickReadyButton: () -> Unit) {
        this.onRefreshButtonClick = onClickReadyButton
    }

    fun setOnShowListener(onShow: () -> Unit) {
        this.onShow = onShow
    }

    fun setMessage(message: String) {
        this.message = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val DIALOG_TAG = "error_dialog"
    }
}