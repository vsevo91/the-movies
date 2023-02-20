package com.example.movies.presentation.fragments.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.domain.entities.filtering.Order
import com.example.domain.entities.filtering.Type
import com.example.domain.entities.movie.MovieGeneralList
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.R
import com.example.movies.databinding.FragmentMovieListBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.adapters.AdapterForGridMovieList
import com.example.movies.presentation.adapters.AdapterForGridMovieListPaged
import com.example.movies.presentation.extensions.doIfError
import com.example.movies.presentation.utilities.SpaceItemDecoration
import com.example.movies.presentation.viewmodels.MovieListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private var movieGeneralList: MovieGeneralList? = null
    private var title: String? = null
    private var topType: String? = null
    private var isTVSeries: Boolean? = null
    private var countryIdForDynamicCollection: Int? = null
    private var genreIdForDynamicCollection: Int? = null
    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!
    private val vm: MovieListViewModel by viewModels()


    private var _adapterPaged: AdapterForGridMovieListPaged? = null
    private val adapterPaged get() = _adapterPaged!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT < 33) {
                @Suppress("DEPRECATION")
                movieGeneralList = it.getParcelable(ARG_MOVIES_LIST)
            } else {
                movieGeneralList = it.getParcelable(ARG_MOVIES_LIST, MovieGeneralList::class.java)
            }
            title = it.getString(ARG_TITLE)
            topType = it.getString(ARG_TOP_TYPE)
            countryIdForDynamicCollection = it.getInt(ARG_COUNTRY_FOR_DYNAMIC_COLLECTION)
            genreIdForDynamicCollection = it.getInt(ARG_GENRE_FOR_DYNAMIC_COLLECTION)
            isTVSeries = it.getBoolean(ARG_IS_TV_SERIES)
            Log.d(APPLICATION_TAG, "List of movies: $movieGeneralList")
            Log.d(APPLICATION_TAG, "Title: $title")
            Log.d(APPLICATION_TAG, "Type of TOP: $topType")
            Log.d(APPLICATION_TAG, "CountryIdForDynamicCollection: $countryIdForDynamicCollection")
            Log.d(APPLICATION_TAG, "GenreIdForDynamicCollection: $genreIdForDynamicCollection")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.itemAnimator = null
        if (movieGeneralList == null) { //list of movies is received via viewModel from API (PagingData)
            vm.connectionErrorState.observe(viewLifecycleOwner) { isError ->
                if (isError) doIfError(true) {
                    vm.clearErrorState()
                    startGettingMovies()
                }
            }
            vm.otherErrorState.observe(viewLifecycleOwner) { isError ->
                if (isError) doIfError(false) {
                    vm.clearErrorState()
                    startGettingMovies()
                }
            }
            _adapterPaged = AdapterForGridMovieListPaged()

            adapterPaged.setResourceOfAdditionalMovieInfo {
                vm.getMovieFullInfoSource(it)
            }

            adapterPaged.setItemClickListener {
                val bundle = Bundle().apply {
                    putInt(ARG_MOVIE_ID, it.id!!)
                }
                findNavController().navigate(
                    R.id.action_listPageFragment_to_movieInfoFragment,
                    bundle
                )
            }
            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    vm.combinedFlow.collect { pagingData ->
                        adapterPaged.submitData(pagingData)
                    }
                }
            }
            startGettingMovies()
            binding.recyclerView.adapter = adapterPaged
            adapterPaged.loadStateFlow.onEach {
                if (it.source.refresh is LoadState.Error ||
                    it.source.append is LoadState.Error ||
                    it.source.prepend is LoadState.Error
                ) {
                    doIfError(true) {
                        adapterPaged.retry()
                    }
                }
            }.launchIn(lifecycleScope)
        } else { //list of movies is received via parcel from previous fragment
            vm.connectionErrorState.observe(viewLifecycleOwner) { isError ->
                if (isError) doIfError(true) {
                    vm.clearErrorState()
                }
            }
            vm.otherErrorState.observe(viewLifecycleOwner) { isError ->
                if (isError) doIfError(false) {
                    vm.clearErrorState()
                }
            }
            val adapter = AdapterForGridMovieList(movieGeneralList!!.items)
            adapter.setResourceOfAdditionalMovieInfo {
                vm.getMovieFullInfoSource(it)
            }
            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    vm.watchedMovies.collect { watchedMovies ->
                        val checkedMovies = movieGeneralList!!.items.map { movie ->
                            val isWatched = watchedMovies.firstOrNull { it.id == movie.id } != null
                            movie.also {
                                it.isWatched = isWatched
                            }
                        }
                        adapter.submitNewList(checkedMovies)
                    }
                }
            }
            adapter.setItemClickListener { movie ->
                movie.id?.let {
                    val bundle = Bundle().apply {
                        putInt(ARG_MOVIE_ID, movie.id!!)
                    }
                    findNavController().navigate(
                        R.id.action_listPageFragment_to_movieInfoFragment,
                        bundle
                    )
                }
            }
            binding.recyclerView.adapter = adapter
        }
        val divider = SpaceItemDecoration(30, 100)
        binding.recyclerView.addItemDecoration(divider)
        binding.toolbarTitle.text = title
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun startGettingMovies() {
        if (topType != null) {
            vm.getMovieTopStream(topType!!)
        } else if (isTVSeries!!) {
            vm.getMovieFilteredStream(orderType = Order.NUM_VOTE, type = Type.TV_SERIES)
        } else {
            vm.getMovieFilteredStream(
                countryIdForDynamicCollection!!,
                genreIdForDynamicCollection!!
            )
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
        private const val ARG_MOVIES_LIST = "movies_list"
        private const val ARG_TITLE = "title"
        private const val ARG_IS_TV_SERIES = "is_tv_series"
        private const val ARG_MOVIE_ID = "movie_id"
        private const val ARG_TOP_TYPE = "top_type"
        private const val ARG_COUNTRY_FOR_DYNAMIC_COLLECTION = "country_for_dynamic_collection"
        private const val ARG_GENRE_FOR_DYNAMIC_COLLECTION = "genre_for_dynamic_collection"
    }
}
