package com.example.movies.presentation.fragments.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.movies.databinding.FragmentOnboardingItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingItemFragment : Fragment() {

    private var param1: String? = null
    private var param2: Int? = null

    private var _binding: FragmentOnboardingItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_ONBOARDING_MESSAGE)
            param2 = it.getInt(ARG_ONBOARDING_IMAGE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.messageText.text = param1
        param2?.let {
            binding.imageView.setImageResource(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ONBOARDING_MESSAGE = "onboarding message"
        private const val ARG_ONBOARDING_IMAGE_ID = "onboarding image"
    }
}