package com.example.movies.presentation.fragments.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.domain.entities.filtering.Order
import com.example.domain.entities.filtering.SearchSettings
import com.example.domain.entities.filtering.Type
import com.example.movies.R
import com.example.movies.databinding.FragmentSearchSettingsBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.viewmodels.SearchViewModel
import com.google.android.material.slider.RangeSlider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchSettingsFragment : Fragment() {

    private var _binding: FragmentSearchSettingsBinding? = null
    private val binding get() = _binding!!
    private val vm: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var settings: SearchSettings? = null
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                vm.searchSettingsFlow.collect { searchSettings ->
                    settings = searchSettings
                    applySearchSettings(searchSettings)
                }
            }
        }
        binding.checkboxName.text = getString(R.string.show_only_unwatched_movies)
        binding.checkboxLayout.setOnClickListener {
            vm.changeSearchSettingsOnlyUnwatchedMovies()
        }
        binding.toolbar.setNavigationOnClickListener {
            navigateToSearchFragmentAndEnableSearching()
        }
        binding.yearLayout.setOnClickListener {
            findNavController().navigate(R.id.action_searchSettingsFragment_to_choosingYearMainFragment)
        }
        binding.rangeSliderRating.apply {
            values = listOf(
                settings?.ratingFrom?.toFloat() ?: MIN_RATING,
                settings?.ratingTo?.toFloat() ?: MAX_RATING
            )
            valueFrom = MIN_RATING
            valueTo = MAX_RATING
            stepSize = STEP_SIZE_RATING
            addOnSliderTouchListener(makeSliderTouchListener { values ->
                val ratingFrom = values.first().toInt()
                val ratingTo = values.last().toInt()
                vm.changeSearchSettingsRatingRange(ratingFrom, ratingTo)
            })
        }
        binding.countryLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putString(ARG_COUNTRY_OR_GENRE, COUNTRY)
            }
            findNavController().navigate(
                R.id.action_searchSettingsFragment_to_searchSettingsDetailsFragment, bundle
            )
        }
        binding.genreLayout.setOnClickListener {
            val bundle = Bundle().apply {
                putString(ARG_COUNTRY_OR_GENRE, GENRE)
            }
            findNavController().navigate(
                R.id.action_searchSettingsFragment_to_searchSettingsDetailsFragment, bundle
            )
        }
        binding.chipShowGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.first()) {
                binding.chipAll.id -> vm.changeSearchSettingsType(Type.ALL)
                binding.chipMovies.id -> vm.changeSearchSettingsType(Type.FILM)
                binding.chipSeries.id -> vm.changeSearchSettingsType(Type.TV_SERIES)
                else -> throw IllegalStateException()
            }
        }
        binding.chipSortGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.first()) {
                binding.chipDate.id -> vm.changeSearchSettingsSortBy(Order.YEAR)
                binding.chipPopularity.id -> vm.changeSearchSettingsSortBy(Order.NUM_VOTE)
                binding.chipRating.id -> vm.changeSearchSettingsSortBy(Order.RATING)
                else -> throw IllegalStateException()
            }
        }
        binding.applyButton.setOnClickListener {
            navigateToSearchFragmentAndEnableSearching()
        }
        binding.resetButton.setOnClickListener {
            vm.resetAllSettings()
            Snackbar.make(binding.root, getString(R.string.settings_reset), Snackbar.LENGTH_SHORT)
                .show()
        }
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    vm.enableSearching()
                    vm.enableScrollingUp()
                    isEnabled = false
                    Log.d(TAG, "Overridden onBackPressed is working")
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            })
    }

    private fun navigateToSearchFragmentAndEnableSearching() {
        vm.enableSearching()
        vm.enableScrollingUp()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun defineTextForYearOrRatingRange(range: List<Float>): String {
        val firstValue = range.first()
        val secondValue = range.last()
        return if ((firstValue == MIN_YEAR && secondValue == MAX_YEAR) ||
            (firstValue == MIN_RATING && secondValue == MAX_RATING)
        ) {
            getString(R.string.all)
        } else {
            "c ${firstValue.toInt()} по ${secondValue.toInt()}"
        }
    }

    private fun applySearchSettings(settings: SearchSettings) {
        when (settings.type) {
            Type.FILM -> binding.chipMovies.isChecked = true
            Type.TV_SHOW -> Unit
            Type.TV_SERIES -> binding.chipSeries.isChecked = true
            Type.MINI_SERIES -> Unit
            Type.ALL -> binding.chipAll.isChecked = true
        }
        when (settings.sortBy) {
            Order.YEAR -> binding.chipDate.isChecked = true
            Order.NUM_VOTE -> binding.chipPopularity.isChecked = true
            Order.RATING -> binding.chipRating.isChecked = true
        }
        binding.countryChosen.text = if (settings.country != null) {
            settings.country!!.value
        } else {
            getString(R.string.country)
        }
        binding.genreChosen.text = if (settings.genre != null) {
            settings.genre!!.value
        } else {
            getString(R.string.genre)
        }
        val yearRange = listOf(settings.yearFrom.toFloat(), settings.yearTo.toFloat())
        binding.yearChosen.text = defineTextForYearOrRatingRange(yearRange)
        val ratingRange = listOf(settings.ratingFrom.toFloat(), settings.ratingTo.toFloat())
        binding.ratingChosen.text = defineTextForYearOrRatingRange(ratingRange)
        binding.rangeSliderRating.values = ratingRange
        binding.checkbox.isSelected = settings.showOnlyUnwatched
        Log.d(TAG, "Settings:$settings")
    }

    private fun makeSliderTouchListener(onStopTrackingTouch: (List<Float>) -> Unit): RangeSlider.OnSliderTouchListener {
        return object :
            RangeSlider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: RangeSlider) = Unit

            override fun onStopTrackingTouch(slider: RangeSlider) {
                val values = slider.values
                onStopTrackingTouch(values)
                Log.d(TAG, values.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).turnOffStatusBarTransparency()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).turnOffStatusBarTransparency()
    }

    companion object {
        private const val TAG = "MoviesAppTAG"
        private const val MIN_YEAR = 1888F
        private const val MAX_YEAR = 2023F
        private const val MIN_RATING = 1F
        private const val MAX_RATING = 10F
        private const val STEP_SIZE_RATING = 1F
        private const val ARG_COUNTRY_OR_GENRE = "country_or_genre"
        private const val COUNTRY = "COUNTRY"
        private const val GENRE = "GENRE"
    }
}