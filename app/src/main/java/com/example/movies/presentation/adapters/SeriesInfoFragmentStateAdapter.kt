package com.example.movies.presentation.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.domain.entities.series.Series
import com.example.movies.presentation.fragments.home.SeriesInfoItemFragment


class SeriesInfoFragmentStateAdapter(
    fragment: Fragment,
    private var series: Series,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = series.total

    override fun createFragment(position: Int): Fragment {
        val fragment = SeriesInfoItemFragment()
        val season = series.items[position]
        fragment.arguments = Bundle().apply {
            putParcelable(ARG_SEASON, season)
        }
        return fragment
    }

    companion object {
        private const val ARG_SEASON = "season"
    }
}