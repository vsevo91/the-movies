package com.example.movies.presentation.fragments.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.movies.R
import com.example.movies.databinding.FragmentOnboardingMainBinding
import com.example.movies.presentation.adapters.OnboardingFragmentStateAdapter
import com.example.movies.presentation.viewmodels.OnboardingViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingMainFragment : Fragment() {

    private var _binding: FragmentOnboardingMainBinding? = null
    private val binding get() = _binding!!
    private val vm: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.pager
        val tabLayout = binding.tabLayout

        val adapter = OnboardingFragmentStateAdapter(this)

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        binding.skipText.setOnClickListener {
            vm.submitEntrance()
            findNavController().navigate(R.id.action_onboardingFragment_to_mainFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}