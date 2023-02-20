package com.example.movies.presentation.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.movies.R
import com.example.movies.presentation.fragments.onboarding.OnboardingItemFragment

class OnboardingFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val listOfOnboardingMessages = listOf(
        fragment.resources.getString(R.string.onboarding_message_1),
        fragment.resources.getString(R.string.onboarding_message_2),
        fragment.resources.getString(R.string.onboarding_message_3)
    )

    private val listOfOnboardingImages = listOf(
        R.raw.onboarding_1,
        R.raw.onboarding_2,
        R.raw.onboarding_3
    )

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = OnboardingItemFragment()
        fragment.arguments = Bundle().apply {
            putString(ARG_ONBOARDING_MESSAGE, listOfOnboardingMessages[position])
            putInt(ARG_ONBOARDING_IMAGE_ID, listOfOnboardingImages[position])
        }
        return fragment
    }

    companion object {
        private const val ARG_ONBOARDING_MESSAGE = "onboarding message"
        private const val ARG_ONBOARDING_IMAGE_ID = "onboarding image"
    }
}