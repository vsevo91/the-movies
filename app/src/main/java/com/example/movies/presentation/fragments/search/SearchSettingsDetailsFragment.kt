package com.example.movies.presentation.fragments.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.domain.entities.filtering.CountryForFiltering
import com.example.domain.entities.filtering.GenreForFiltering
import com.example.movies.R
import com.example.movies.databinding.FragmentSearchSettingsDetailsBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.AdapterForSearchSettingsDetails
import com.example.movies.presentation.viewmodels.SearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchSettingsDetailsFragment : Fragment() {

    private var countryOrGenre: String? = null
    private var param2: String? = null
    private var _binding: FragmentSearchSettingsDetailsBinding? = null
    private val binding get() = _binding!!
    private val vm: SearchViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            countryOrGenre = it.getString(ARG_COUNTRY_OR_GENRE)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSettingsDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerView
        val adapter = AdapterForSearchSettingsDetails()
        recyclerView.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.genresAndCountriesForFilteringFlow.collect { genresAndCountriesForFiltering ->
                    when (countryOrGenre) {
                        COUNTRY -> {
                            val list =
                                genresAndCountriesForFiltering?.countries?.sortedBy { it.value }
                            adapter.submitList(list)
                            Log.d(TAG, "${list!!.map { it }}")
                        }
                        GENRE -> {
                            val list = genresAndCountriesForFiltering?.genres?.sortedBy { it.value }
                            adapter.submitList(list)
                            Log.d(TAG, "${list!!.map { it }}")
                        }
                        else -> throw IllegalStateException()
                    }
                }
            }
        }
        vm.getGenresAndCountriesFiltered("")
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        when (countryOrGenre) {
            COUNTRY -> {
                binding.toolbarTitle.text = getString(R.string.country)
                binding.searchBar.hint = getString(R.string.hint_for_search_bar_settings_countries)
            }
            GENRE -> {
                binding.toolbarTitle.text = getString(R.string.genre)
                binding.searchBar.hint = getString(R.string.hint_for_search_bar_settings_genres)
            }
            else -> throw IllegalStateException()
        }
        binding.searchBar.addTextChangedListener {
            Log.d(TAG, "Search bar was changed")
            val filter = it.toString()
            vm.getGenresAndCountriesFiltered(filter)
        }
        adapter.setOnItemClick { item, itemView ->
            Log.d(TAG, "Position1: $item")
            when (countryOrGenre) {
                COUNTRY -> {
                    vm.changeSearchSettingsCountry(item as CountryForFiltering)
                }
                GENRE -> {
                    vm.changeSearchSettingsGenre(item as GenreForFiltering)
                }
                else -> throw IllegalStateException()
            }
            recyclerView.forEach {
                it.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            adapter.rememberChosenItem(item)
            itemView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.light_gray
                )
            )
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.resetButton.setOnClickListener {
            when (countryOrGenre) {
                COUNTRY -> {
                    vm.changeSearchSettingsCountry(null)
                    Snackbar.make(binding.root, getString(R.string.settings_country_reset), Snackbar.LENGTH_SHORT)
                        .show()
                }
                GENRE -> {
                    vm.changeSearchSettingsGenre(null)
                    Snackbar.make(binding.root, getString(R.string.settings_genre_reset), Snackbar.LENGTH_SHORT)
                        .show()
                }
                else -> throw IllegalStateException()
            }
            requireActivity().onBackPressedDispatcher.onBackPressed()
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
        private const val ARG_COUNTRY_OR_GENRE = "country_or_genre"
        private const val ARG_PARAM2 = "param2"
        private const val COUNTRY = "COUNTRY"
        private const val GENRE = "GENRE"
    }
}