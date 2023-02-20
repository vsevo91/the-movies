package com.example.movies.presentation.fragments.search

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.R
import com.example.movies.databinding.FragmentChoosingYearItemBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.ChoosingYearFragmentStateAdapter
import com.example.movies.presentation.viewmodels.SearchViewModel
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChoosingYearItemFragment : Fragment() {

    private var currentRange: IntArray? = null
    private var unavailableRange: IntArray? = null
    private var typeString: String? = null
    private var type: ChoosingYearFragmentStateAdapter.Type? = null
    private var _binding: FragmentChoosingYearItemBinding? = null
    private val binding get() = _binding!!
    private val vm: SearchViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentRange = it.getIntArray(ARG_CURRENT_RANGE)
            unavailableRange = it.getIntArray(ARG_UNAVAILABLE_RANGE)
            typeString = it.getString(ARG_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChoosingYearItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        defineWidthOfChips()
        parentFragment?.startPostponedEnterTransition()
        defineType(typeString!!)
        binding.chipGroup.children.forEachIndexed { index, chip ->
            val currentValue = currentRange!![0] + index
            (chip as Chip).text = "$currentValue"
            unavailableRange?.let {
                if (currentValue <= it[1]) {
                    chip.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.light_gray
                        )
                    )
                    chip.isClickable = false
                }
            }
        }
        @SuppressLint("SetTextI18n")
        binding.period.text = "${currentRange!![0]} - ${currentRange!![1]}"
        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val checkedChip = group.children.firstOrNull { it.id == checkedIds[0] } as Chip?
                val chosenYear = checkedChip?.text?.toString()?.toInt()
                chosenYear?.let { year ->
                    vm.selectYear(year, type!!)
                }
            }
        }
        when (type) {
            ChoosingYearFragmentStateAdapter.Type.SEARCH_FROM -> {
                vm.preliminaryChosenYearFromLiveData.observe(viewLifecycleOwner) { year ->
                    if (year != null) {
                        selectExactYear(year)
                    } else {
                        unselectAll()
                    }
                }
            }
            ChoosingYearFragmentStateAdapter.Type.SEARCH_TO -> {
                vm.preliminaryChosenYearToLiveData.observe(viewLifecycleOwner) { year ->
                    if (year != null) {
                        selectExactYear(year)
                    } else {
                        unselectAll()
                    }
                }
            }
            null -> throw IllegalStateException()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun defineWidthOfChips() {
        val onPreDrawListener = ViewTreeObserver.OnPreDrawListener {
            if (_binding == null) return@OnPreDrawListener true
            Log.d(APPLICATION_TAG, "Width and height of chips were set")
            binding.chipGroup.children.forEach { chip ->
                val neededWidth = (binding.container.width / 3.5).toInt()
                val height = chip.height
                val layoutParams = LayoutParams(neededWidth, height)
                (chip as Chip).layoutParams = layoutParams
            }
            true
        }
        val onDrawListener = ViewTreeObserver.OnDrawListener{
            if (_binding == null) return@OnDrawListener
            binding.root.viewTreeObserver.removeOnPreDrawListener(onPreDrawListener)
        }
        binding.root.viewTreeObserver.addOnPreDrawListener(onPreDrawListener)
        binding.chipGroup.viewTreeObserver.addOnDrawListener(onDrawListener)
    }

    private fun selectExactYear(year: Int): Boolean {
        val itemId =
            binding.chipGroup.children.firstOrNull {
                (it as Chip).text.toString().toInt() == year
            }?.id
        return if (itemId != null) {
            binding.chipGroup.check(itemId)
            true
        } else {
            unselectAll()
            false
        }
    }

    private fun unselectAll() {
        binding.chipGroup.clearCheck()
    }

    private fun defineType(typeString: String): ChoosingYearFragmentStateAdapter.Type {
        val type = when (typeString) {
            ChoosingYearFragmentStateAdapter.Type.SEARCH_FROM.name -> {
                ChoosingYearFragmentStateAdapter.Type.SEARCH_FROM
            }
            ChoosingYearFragmentStateAdapter.Type.SEARCH_TO.name -> {
                ChoosingYearFragmentStateAdapter.Type.SEARCH_TO
            }
            else -> throw IllegalStateException()
        }
        this.type = type
        return this.type!!
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
        private const val ARG_CURRENT_RANGE = "current_range"
        private const val ARG_UNAVAILABLE_RANGE = "unavailable_range"
        private const val ARG_TYPE = "type"

        @JvmStatic
        fun newInstance(
            currentRange: IntArray,
            unavailableRange: IntArray?,
            type: String
        ) =
            ChoosingYearItemFragment().apply {
                arguments = Bundle().apply {
                    putIntArray(ARG_CURRENT_RANGE, currentRange)
                    unavailableRange?.let { putIntArray(ARG_UNAVAILABLE_RANGE, it) }
                    putString(ARG_TYPE, type)
                }
            }
    }
}