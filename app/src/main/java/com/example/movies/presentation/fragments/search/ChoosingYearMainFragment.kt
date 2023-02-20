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
import androidx.viewpager2.widget.ViewPager2
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.SEARCH_MIN_YEAR
import com.example.movies.R
import com.example.movies.databinding.FragmentChoosingYearMainBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.ChoosingYearFragmentStateAdapter
import com.example.movies.presentation.viewmodels.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.floor

@AndroidEntryPoint
class ChoosingYearMainFragment : Fragment() {

    private var _binding: FragmentChoosingYearMainBinding? = null
    private val binding get() = _binding!!
    private var chosenYearFrom: Int? = null
    private var chosenYearTo: Int? = null
    private val vm: SearchViewModel by activityViewModels()
    private val maxYear = defineCurrentYear()
    private val minYear = SEARCH_MIN_YEAR

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChoosingYearMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        binding.resetButton.setOnClickListener {
            vm.setDefaultYearRange()
            Snackbar.make(
                binding.root,
                getString(R.string.settings_year_range_reset),
                Snackbar.LENGTH_SHORT
            ).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isEnabled) {
                        isEnabled = false
                        vm.resetPreliminaryYearSelection()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
        vm.preliminaryChosenYearFromLiveData.observe(viewLifecycleOwner) { yearFrom ->
            chosenYearFrom = yearFrom
        }
        vm.preliminaryChosenYearToLiveData.observe(viewLifecycleOwner) { yearTo ->
            chosenYearTo = yearTo
        }
        binding.toolbarTitle.text = getString(R.string.search_range)
        binding.toolbar.setNavigationOnClickListener {
            vm.resetPreliminaryYearSelection()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val viewPagerYearFrom = initializeViewPager(
            binding.viewPagerFrom,
            ChoosingYearFragmentStateAdapter.Type.SEARCH_FROM
        )
        val viewPagerYearTo = initializeViewPager(
            binding.viewPagerTo,
            ChoosingYearFragmentStateAdapter.Type.SEARCH_TO
        )
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                vm.searchSettingsFlow.collect { searchSettings ->
                    Log.d(APPLICATION_TAG, searchSettings.toString())
                    val isDefault =
                        searchSettings.yearTo == maxYear && searchSettings.yearFrom == minYear
                    goToCurrentPage(
                        searchSettings.yearFrom,
                        viewPagerYearFrom,
                        isDefault
                    )
                    goToCurrentPage(
                        searchSettings.yearTo,
                        viewPagerYearTo,
                        isDefault
                    )
                }
            }
        }
        binding.applyButton.setOnClickListener {
            when {
                chosenYearFrom == null && chosenYearTo == null -> makeSnack(getString(R.string.choose_period))
                chosenYearFrom == null -> makeSnack(getString(R.string.choose_start_year))
                chosenYearTo == null -> makeSnack(getString(R.string.choose_finish_year))
                chosenYearFrom!! > chosenYearTo!! -> makeSnack(getString(R.string.start_year_can_not_be_less_than_finish))
                else -> {
                    vm.changeSearchSettingsYearRange(chosenYearFrom!!, chosenYearTo!!)
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).turnOffStatusBarTransparency()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).turnOffStatusBarTransparency()
    }

    private fun initializeViewPager(
        viewPager: ViewPager2,
        type: ChoosingYearFragmentStateAdapter.Type
    ): ViewPager2 {
        val adapter =
            ChoosingYearFragmentStateAdapter(this, minYear, maxYear, type)
        viewPager.adapter = adapter
        return viewPager
    }

    private fun goToCurrentPage(inputYear: Int, viewPager: ViewPager2, isDefault: Boolean) {
        val pageCount = (viewPager.adapter as ChoosingYearFragmentStateAdapter).itemCount
        val currentPage =
            if (isDefault) {
                pageCount
            } else {
                val yearsPerPage = 12
                (pageCount - floor(
                    ((maxYear.toDouble() - inputYear.toDouble())
                            / yearsPerPage.toDouble())
                ) - 1).toInt()
            }
        viewPager.setCurrentItem(currentPage, false)
    }

    private fun makeSnack(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun defineCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        return calendar.get(Calendar.YEAR)
    }
}