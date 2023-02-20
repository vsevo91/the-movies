package com.example.movies.presentation.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager.LayoutParams
import androidx.fragment.app.DialogFragment
import com.example.movies.R
import com.example.movies.databinding.FragmentDialogCreateUserCollectionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateUserCollectionDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogCreateUserCollectionBinding? = null
    private val binding get() = _binding!!
    private var onClickReadyButton: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = requireActivity().layoutInflater
        _binding = FragmentDialogCreateUserCollectionBinding.inflate(inflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.Theme_AppCompat_Dialog_Custom)
            builder.setView(binding.root)
            val dialog = builder.create()
            binding.editText.requestFocus()
            dialog.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            binding.readyButton.setOnClickListener {
                val collectionName = binding.editText.text.toString()
                onClickReadyButton?.invoke(collectionName)
            }
            dialog
        } ?: throw IllegalStateException()
    }

    fun setOnReadyButtonClickListener(onClickReadyButton: (String) -> Unit) {
        this.onClickReadyButton = onClickReadyButton
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        const val DIALOG_TAG = "create_new_collection_dialog"
    }
}