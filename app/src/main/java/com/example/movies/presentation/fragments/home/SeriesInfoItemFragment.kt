package com.example.movies.presentation.fragments.home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.domain.entities.series.Season
import com.example.movies.R
import com.example.movies.databinding.FragmentSeriesInfoItemBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.AdapterForVerticalSeriesList
import com.example.movies.presentation.utilities.SpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeriesInfoItemFragment : Fragment() {

    private var season: Season? = null
    private var _binding: FragmentSeriesInfoItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT < 33) {
                @Suppress("DEPRECATION")
                season = it.getParcelable(ARG_SEASON)
            } else {
                season = it.getParcelable(ARG_SEASON, Season::class.java)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeriesInfoItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (season == null) return
        val listOfEpisodes = season!!.episodes
        val currentLocale = resources.configuration.locales[0].country
        val adapter = AdapterForVerticalSeriesList(currentLocale, listOfEpisodes, resources)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(SpaceItemDecoration(0, 40))
        val numberOfEpisodes = season!!.episodes.size
        val seasonNumber = season!!.number
        binding.seasonAndEpisodes.text = getString(
            R.string.season_n_episodes_n,
            seasonNumber,
            resources.getQuantityString(
                R.plurals.count_episodes,
                numberOfEpisodes,
                numberOfEpisodes
            )
        )
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
        private const val ARG_SEASON = "season"
    }
}