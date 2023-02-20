package com.example.movies.presentation.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movies.databinding.FragmentProfileRootBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileRootFragment : Fragment() {

    private var _binding: FragmentProfileRootBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileRootBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}