package com.example.movies.presentation.fragments.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movies.databinding.FragmentSearchRootBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchRootFragment : Fragment() {

    private var _binding: FragmentSearchRootBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchRootBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}