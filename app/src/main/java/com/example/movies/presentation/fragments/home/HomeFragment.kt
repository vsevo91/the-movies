package com.example.movies.presentation.fragments.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.entities.movie.MovieGeneralList
import com.example.domain.utilities.APPLICATION_TAG
import com.example.movies.R
import com.example.movies.databinding.FragmentHomeBinding
import com.example.movies.presentation.activities.MainActivity
import com.example.movies.presentation.extensions.doIfError
import com.example.movies.presentation.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var countryIdForDynamicCollection1: Int? = null
    private var genreIdForDynamicCollection1: Int? = null
    private var countryIdForDynamicCollection2: Int? = null
    private var genreIdForDynamicCollection2: Int? = null
    private val vm: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.connectionErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) doIfError(true){
                vm.clearErrorState()
                startGettingMovies()
            }
        }
        vm.otherErrorState.observe(viewLifecycleOwner) { isError ->
            if (isError) doIfError(false){
                vm.clearErrorState()
                startGettingMovies()
            }
        }
        turnOnPlaceholdersForAllViews()
        startObservingMovies()
        setNamesForAllViews()
        setResourcesOfAdditionalInfoForAllViews()
        setActionsOnButtonClickForAllViews()
        setActionsOnItemClickForAllViews()
        startGettingMovies()
        vm.countryIdAndGenreIdForDynamicCollection1.observe(viewLifecycleOwner) { countryIdAndGenreId ->
            countryIdForDynamicCollection1 = countryIdAndGenreId.first()
            genreIdForDynamicCollection1 = countryIdAndGenreId.last()
        }
        vm.countryIdAndGenreIdForDynamicCollection2.observe(viewLifecycleOwner) { countryIdAndGenreId ->
            countryIdForDynamicCollection2 = countryIdAndGenreId.first()
            genreIdForDynamicCollection2 = countryIdAndGenreId.last()
        }
    }

    private fun turnOnPlaceholdersForAllViews() {
        binding.premieresList.turnOnPlaceholders()
        binding.popularList.turnOnPlaceholders()
        binding.dynamicCollection1.turnOnPlaceholders()
        binding.dynamicCollection2.turnOnPlaceholders()
        binding.movieTop250.turnOnPlaceholders()
        binding.series.turnOnPlaceholders()

    }

    private fun startObservingMovies() {
        vm.combinedPremieresList.observe(viewLifecycleOwner) {
            binding.premieresList.setNewList(it)
        }
        vm.combinedPopularMovieList.observe(viewLifecycleOwner) {
            binding.popularList.setNewList(it)
        }
        vm.combinedMovieListCollection1.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.dynamicCollection1.setNewList(it)
            } else {
                vm.getMovieForDynamicCollection1()
            }
        }
        vm.combinedMovieListCollection2.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.dynamicCollection2.setNewList(it)
            } else {
                vm.getMovieForDynamicCollection2()
            }
        }
        vm.combinedMovieTop250List.observe(viewLifecycleOwner) {
            binding.movieTop250.setNewList(it)
        }
        vm.combinedSeries.observe(viewLifecycleOwner) {
            binding.series.setNewList(it)
        }
    }

    private fun setNamesForAllViews() {
        binding.premieresList.setName(getString(R.string.premieres))
        binding.popularList.setName(getString(R.string.popular))
        binding.movieTop250.setName(getString(R.string.top250))
        vm.titleForDynamicCollection1.observe(viewLifecycleOwner) { rawTitle ->
            binding.dynamicCollection1.setName(makeTitle(rawTitle))
        }
        vm.titleForDynamicCollection2.observe(viewLifecycleOwner) { rawTitle ->
            binding.dynamicCollection2.setName(makeTitle(rawTitle))
        }
        binding.series.setName(getString(R.string.series))
    }

    private fun setActionsOnItemClickForAllViews() {
        binding.premieresList.setActionOnItemClick { movie ->
            navigateToMovieInfoFragment(movie)
        }
        binding.popularList.setActionOnItemClick { movie ->
            navigateToMovieInfoFragment(movie)
        }
        binding.dynamicCollection1.setActionOnItemClick { movie ->
            navigateToMovieInfoFragment(movie)
        }
        binding.dynamicCollection2.setActionOnItemClick { movie ->
            navigateToMovieInfoFragment(movie)
        }
        binding.movieTop250.setActionOnItemClick { movie ->
            navigateToMovieInfoFragment(movie)
        }
        binding.series.setActionOnItemClick { movie ->
            navigateToMovieInfoFragment(movie)
        }
    }

    private fun setActionsOnButtonClickForAllViews() {
        binding.premieresList.setActionOnButtonClick { title, listOfMovies ->
            navigateToMovieListFragment(
                title = title,
                listOfMovies = listOfMovies
            )
        }
        binding.popularList.setActionOnButtonClick { title, _ ->
            navigateToMovieListFragment(
                title = title,
                topType = TOP_100_POPULAR_FILMS,
            )
        }
        binding.dynamicCollection1.setActionOnButtonClick { title, _ ->
            navigateToMovieListFragment(
                title = title,
                countryIdForDynamicCollection = countryIdForDynamicCollection1,
                genreIdForDynamicCollection = genreIdForDynamicCollection1
            )
        }
        binding.dynamicCollection2.setActionOnButtonClick { title, _ ->
            navigateToMovieListFragment(
                title = title,
                countryIdForDynamicCollection = countryIdForDynamicCollection2,
                genreIdForDynamicCollection = genreIdForDynamicCollection2
            )
        }
        binding.movieTop250.setActionOnButtonClick { title, _ ->
            navigateToMovieListFragment(
                title = title,
                topType = TOP_250_BEST_FILMS,
            )
        }

        binding.series.setActionOnButtonClick { title, _ ->
            navigateToMovieListFragment(
                title = title,
                isTVSeries = true
            )
        }
    }

    private fun startGettingMovies() {
        vm.getPremieresListForPreview()
        vm.getPopularMovieListForPreview()
        vm.getMovieForDynamicCollection1()
        vm.getMovieForDynamicCollection2()
        vm.getTop250Movies()
        vm.getTVSeries()
    }

    private fun setResourcesOfAdditionalInfoForAllViews() {
        binding.premieresList.setResourceOfAdditionalMovieInfo { movieId ->
            vm.getMovieFullInfo(movieId)
        }
    }

    private fun makeTitle(rawTitle: String): String {
        val genre = rawTitle.substringBefore(" ")
        val country = rawTitle.substringAfter(" ")
        val firstPart = when (genre) {
            HomeViewModel.Companion.TopGenres.THRILLER.nameRu -> getString(R.string.thriller)
            HomeViewModel.Companion.TopGenres.DRAMA.nameRu -> getString(R.string.drama)
            HomeViewModel.Companion.TopGenres.CRIMINAL.nameRu -> getString(R.string.criminal)
            HomeViewModel.Companion.TopGenres.MELODRAMA.nameRu -> getString(R.string.melodrama)
            HomeViewModel.Companion.TopGenres.DETECTIVE.nameRu -> getString(R.string.detective)
            HomeViewModel.Companion.TopGenres.FICTION.nameRu -> getString(R.string.fiction)
            HomeViewModel.Companion.TopGenres.ADVENTURE.nameRu -> getString(R.string.adventure)
            HomeViewModel.Companion.TopGenres.ACTION.nameRu -> getString(R.string.action)
            HomeViewModel.Companion.TopGenres.FANTASY.nameRu -> getString(R.string.fantasy)
            HomeViewModel.Companion.TopGenres.COMEDY.nameRu -> getString(R.string.comedy)
            HomeViewModel.Companion.TopGenres.HORROR.nameRu -> getString(R.string.horror)
            HomeViewModel.Companion.TopGenres.CARTOON.nameRu -> getString(R.string.cartoon)
            HomeViewModel.Companion.TopGenres.FAMILY.nameRu -> getString(R.string.family)
            HomeViewModel.Companion.TopGenres.ANIME.nameRu -> getString(R.string.anime)
            HomeViewModel.Companion.TopGenres.CHILDREN.nameRu -> getString(R.string.children)
            else -> throw IllegalStateException()
        }
        val secondPart = when (country) {
            HomeViewModel.Companion.TopCountries.USA.nameRu -> getString(R.string.usa)
            HomeViewModel.Companion.TopCountries.FRANCE.nameRu -> getString(R.string.france)
            HomeViewModel.Companion.TopCountries.POLAND.nameRu -> getString(R.string.poland)
            HomeViewModel.Companion.TopCountries.UK.nameRu -> getString(R.string.uk)
            HomeViewModel.Companion.TopCountries.SWEDEN.nameRu -> getString(R.string.sweden)
            HomeViewModel.Companion.TopCountries.INDIA.nameRu -> getString(R.string.india)
            HomeViewModel.Companion.TopCountries.SPAIN.nameRu -> getString(R.string.spain)
            HomeViewModel.Companion.TopCountries.GERMANY.nameRu -> getString(R.string.germany)
            HomeViewModel.Companion.TopCountries.ITALY.nameRu -> getString(R.string.italy)
            HomeViewModel.Companion.TopCountries.JAPAN.nameRu -> getString(R.string.japan)
            HomeViewModel.Companion.TopCountries.CHINA.nameRu -> getString(R.string.china)
            HomeViewModel.Companion.TopCountries.DENMARK.nameRu -> getString(R.string.denmark)
            HomeViewModel.Companion.TopCountries.RUSSIA.nameRu -> getString(R.string.russia)
            else -> throw IllegalStateException()
        }
        return firstPart + secondPart
    }

    private fun navigateToMovieListFragment(
        title: String,
        listOfMovies: List<MovieGeneral>? = null,
        topType: String? = null,
        countryIdForDynamicCollection: Int? = null,
        genreIdForDynamicCollection: Int? = null,
        isTVSeries: Boolean = false
    ) {
        val bundle = Bundle().apply {
            putString(ARG_TITLE, title)
            if (listOfMovies != null) {
                val moviesListParcelable = MovieGeneralList(items = listOfMovies)
                Log.d(APPLICATION_TAG, moviesListParcelable.items.toString())
                putParcelable(ARG_MOVIES_LIST, moviesListParcelable)
            }
            if (topType != null) {
                putString(ARG_TOP_TYPE, topType)
            }
            if (countryIdForDynamicCollection != null && genreIdForDynamicCollection != null) {
                putInt(ARG_COUNTRY_FOR_DYNAMIC_COLLECTION, countryIdForDynamicCollection)
                putInt(ARG_GENRE_FOR_DYNAMIC_COLLECTION, genreIdForDynamicCollection)
            }
            if (isTVSeries) {
                putBoolean(ARG_IS_TV_SERIES, true)
            }
        }
        findNavController().navigate(R.id.action_homeFragment_to_listPageFragment, bundle)
    }

    private fun navigateToMovieInfoFragment(movie: MovieGeneral) {
        val id = movie.id
        val bundle = Bundle().apply {
            putInt(ARG_MOVIE_ID, id!!)
        }
        findNavController().navigate(R.id.action_homeFragment_to_movieInfoFragment, bundle)
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
        private const val ARG_MOVIE_ID = "movie_id"
        private const val TOP_100_POPULAR_FILMS = "TOP_100_POPULAR_FILMS"
        private const val TOP_250_BEST_FILMS = "TOP_250_BEST_FILMS"
        private const val ARG_TOP_TYPE = "top_type"
        private const val ARG_IS_TV_SERIES = "is_tv_series"
        private const val ARG_COUNTRY_FOR_DYNAMIC_COLLECTION = "country_for_dynamic_collection"
        private const val ARG_GENRE_FOR_DYNAMIC_COLLECTION = "genre_for_dynamic_collection"
    }
}