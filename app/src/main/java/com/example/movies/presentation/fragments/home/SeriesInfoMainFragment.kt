package com.example.movies.presentation.fragments.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.domain.entities.series.Series
import com.example.movies.databinding.FragmentSeriesInfoMainBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.SeriesInfoFragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeriesInfoMainFragment : Fragment() {

    private var series: Series? = null
    private var title: String? = null
    private var _binding: FragmentSeriesInfoMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT < 33) {
                @Suppress("DEPRECATION")
                series = it.getParcelable(ARG_SERIES)
            } else {
                series = it.getParcelable(ARG_SERIES, Series::class.java)
            }
            title = it.getString(ARG_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesInfoMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (series == null) {
            Log.d(TAG, "series is null!!")
            return
        }
        binding.toolbarTitle.text = title
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        val pager = binding.pager
        val tabLayout = binding.tabLayout
        val adapter = SeriesInfoFragmentStateAdapter(this, series!!)
        pager.adapter = adapter
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = "${series!!.items[position].number}"
        }.attach()
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
        private const val ARG_SERIES = "series"
        private const val ARG_TITLE = "title"
    }
}