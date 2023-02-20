package com.example.movies.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.movies.presentation.fragments.search.ChoosingYearItemFragment
import kotlin.math.ceil


class ChoosingYearFragmentStateAdapter(
    fragment: Fragment,
    private val yearFrom: Int,
    private val yearTo: Int,
    private val type: Type
) : FragmentStateAdapter(fragment) {

    enum class Type {
        SEARCH_FROM,
        SEARCH_TO
    }

    private val itemCount = defineItemCount()

    override fun getItemCount(): Int = itemCount

    override fun createFragment(position: Int): Fragment {
        val currentPage = position + 1
        val currentRange = defineCurrentRange(currentPage)
        val currentUnavailableRange = defineCurrentUnavailableRange(currentRange)
        val typeString = type.name
        return ChoosingYearItemFragment.newInstance(
            currentRange,
            currentUnavailableRange,
            typeString
        )
    }

    private fun defineCurrentRange(currentPage: Int): IntArray {
        val secondValue = yearTo - (itemCount - currentPage) * 12
        val firstValue = secondValue - 11
        return intArrayOf(firstValue, secondValue)
    }

    private fun defineCurrentUnavailableRange(currentRange: IntArray): IntArray? {
        return if (currentRange[0] >= yearFrom) {
            null
        } else {
            val firstValue = currentRange[0]
            val secondValue = yearFrom - 1
            return intArrayOf(firstValue, secondValue)
        }
    }

    private fun defineItemCount(): Int {
        return ceil((yearTo - yearFrom).toDouble() / 12).toInt()
    }
}