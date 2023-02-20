package com.example.movies.presentation.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.example.domain.entities.filtering.*
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.usecases.GetGenresAndCountriesForFilteringUseCase
import com.example.domain.usecases.GetMovieFilteredListAsMovieGeneralListStreamUseCase
import com.example.domain.usecases.GetUserCollectionsUseCase
import com.example.domain.utilities.*
import com.example.movies.presentation.adapters.ChoosingYearFragmentStateAdapter
import com.example.movies.presentation.extensions.makeQuerySafely
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getMovieFilteredListAsMovieGeneralListStreamUseCase: GetMovieFilteredListAsMovieGeneralListStreamUseCase,
    private val getGenresAndCountriesForFilteringUseCase: GetGenresAndCountriesForFilteringUseCase,
    getUserCollectionsUseCase: GetUserCollectionsUseCase
) : ViewModel() {

    private val _connectionErrorState = MutableLiveData(false)
    val connectionErrorState: LiveData<Boolean> = _connectionErrorState

    private val _otherErrorState = MutableLiveData(false)
    val otherErrorState: LiveData<Boolean> = _otherErrorState

    init {
        getGenresAndCountriesForFiltering()
    }

    private val _watchedMovies = getUserCollectionsUseCase.execute().map { userCollections ->
        userCollections.firstOrNull { userCollection ->
            userCollection.id == WATCHED_COLLECTION_ID
        }?.movies
    }

    private var _isScrollingUpEnabled = MutableStateFlow(false)
    val isScrollingUpEnabled = _isScrollingUpEnabled.asStateFlow()
    private var isSearchingEnabled = false
    private var genresAndCountriesForFiltering: GenresAndCountriesForFiltering? = null
    private val currentYear = defineCurrentYear()
    private val defaultSearchSettings = SearchSettings(
        type = Type.ALL,
        country = null,
        genre = null,
        yearFrom = SEARCH_MIN_YEAR,
        yearTo = currentYear,
        ratingFrom = SEARCH_MIN_RATING,
        ratingTo = SEARCH_MAX_RATING,
        sortBy = Order.NUM_VOTE,
        keyword = "",
        showOnlyUnwatched = false
    )
    private var searchSettings = defaultSearchSettings
    private val _genresAndCountriesForFilteringFlow =
        MutableStateFlow<GenresAndCountriesForFiltering?>(null)
    val genresAndCountriesForFilteringFlow = _genresAndCountriesForFilteringFlow.asStateFlow()
    private val _searchSettingsFlow = MutableStateFlow(searchSettings)
    val searchSettingsFlow = _searchSettingsFlow.asStateFlow()
    private val handler = Handler(Looper.getMainLooper())
    private var doWithDelay: Runnable? = null
    private var streamOfMovies: Flow<PagingData<MovieGeneral>>? = null
    private val _streamOfMoviesLiveData = MutableLiveData<Flow<PagingData<MovieGeneral>>>()
    val filteredStreamOdMovies = combine(
        _watchedMovies,
        _streamOfMoviesLiveData.asFlow(),
        _searchSettingsFlow
    ) { watchedMovies, flowOfPagingData, searchSettings ->
        if (searchSettings.showOnlyUnwatched) {
            flowOfPagingData.map { pagingData ->
                pagingData.filter { movieFromPagingData ->
                    val isWatched =
                        watchedMovies?.firstOrNull { it.id == movieFromPagingData.id } != null
                    !isWatched
                }
            }
        } else {
            flowOfPagingData.map { pagingData ->
                pagingData.map { movie ->
                    val isWatched = watchedMovies?.firstOrNull { it.id == movie.id } != null
                    movie.apply { this@apply.isWatched = isWatched }
                }
            }
        }
    }
    private var _preliminaryChosenYearFromLiveData = MutableLiveData<Int?>()
    val preliminaryChosenYearFromLiveData: LiveData<Int?> get() = _preliminaryChosenYearFromLiveData
    private var _preliminaryChosenYearToLiveData = MutableLiveData<Int?>()
    val preliminaryChosenYearToLiveData: LiveData<Int?> get() = _preliminaryChosenYearToLiveData


//    private val _isLoadingStateLiveData = MutableLiveData<Boolean>()
//    val isLoadingStateLiveData: LiveData<Boolean> get() = _isLoadingStateLiveData

    fun tryToGetMovieFilteredStreamWithDelay() {
        Log.d(APPLICATION_TAG, "Searching enabled:$isSearchingEnabled")
        Log.d(APPLICATION_TAG, "Scrolling enabled:${isScrollingUpEnabled.value}")
        if (!isSearchingEnabled) return
        resetStream()
        if (doWithDelay != null) {
            handler.removeCallbacks(doWithDelay!!)
        }
        doWithDelay = Runnable {
            if (streamOfMovies == null) {
                val result = getMovieFilteredStream()
                streamOfMovies = result
                Log.d(APPLICATION_TAG, "Stream was created")
                _streamOfMoviesLiveData.value = streamOfMovies!!
            }
        }
        handler.postDelayed(doWithDelay!!, SEARCH_DELAY_TIME)
    }

    private fun resetStream() {
        streamOfMovies = null
    }

    private fun getMovieFilteredStream(): Flow<PagingData<MovieGeneral>> {
        Log.d(APPLICATION_TAG, "Searching started with settings: $searchSettings")
        val countries = if (searchSettings.country?.id != null) {
            arrayOf(searchSettings.country!!.id)
        } else {
            null
        }
        val genres = if (searchSettings.genre?.id != null) {
            arrayOf(searchSettings.genre!!.id)
        } else {
            null
        }
        val order = searchSettings.sortBy
        val type = searchSettings.type
        val ratingFrom = searchSettings.ratingFrom
        val ratingTo = searchSettings.ratingTo
        val yearFrom = searchSettings.yearFrom
        val yearTo = searchSettings.yearTo
        val keyword = searchSettings.keyword
        return getMovieFilteredListAsMovieGeneralListStreamUseCase.execute(
            countries = countries,
            genres = genres,
            order = order,
            type = type,
            ratingFrom = ratingFrom,
            ratingTo = ratingTo,
            yearFrom = yearFrom,
            yearTo = yearTo,
            keyword = keyword
        ).cachedIn(viewModelScope)
    }

    fun changeSearchSettingsType(newType: Type) {
        Log.d(APPLICATION_TAG, "New type: $newType")
        val newSettings = searchSettings.copy(type = newType)
        searchSettings = newSettings
        _searchSettingsFlow.value = searchSettings
    }

    fun changeSearchSettingsKeyword(newKeyword: String) {
        Log.d(APPLICATION_TAG, "New keyword: $newKeyword")
        val newSettings = searchSettings.copy(keyword = newKeyword)
        searchSettings = newSettings
        _searchSettingsFlow.value = searchSettings
    }

    fun changeSearchSettingsCountry(newCountry: CountryForFiltering?) {
        val newSettings = searchSettings.copy(country = newCountry)
        searchSettings = newSettings
        _searchSettingsFlow.value = searchSettings
    }

    fun changeSearchSettingsGenre(newGenre: GenreForFiltering?) {
        val newSettings = searchSettings.copy(genre = newGenre)
        searchSettings = newSettings
        _searchSettingsFlow.value = searchSettings
    }

    fun changeSearchSettingsOnlyUnwatchedMovies() {
        val newSettings = if (searchSettings.showOnlyUnwatched) {
            searchSettings.copy(showOnlyUnwatched = false)
        } else {
            searchSettings.copy(showOnlyUnwatched = true)
        }
        searchSettings = newSettings
        _searchSettingsFlow.value = searchSettings
    }

    fun changeSearchSettingsYearRange(newYearFrom: Int, newYearTo: Int) {
        val newSettings = searchSettings.copy(yearFrom = newYearFrom, yearTo = newYearTo)
        searchSettings = newSettings
        _searchSettingsFlow.value = searchSettings
    }

    fun changeSearchSettingsRatingRange(newRatingFrom: Int, newRatingTo: Int) {
        val newSettings = searchSettings.copy(ratingFrom = newRatingFrom, ratingTo = newRatingTo)
        searchSettings = newSettings
        _searchSettingsFlow.value = searchSettings
    }

    fun changeSearchSettingsSortBy(newSortBy: Order) {
        Log.d(APPLICATION_TAG, "New order: $newSortBy")
        val newSettings = searchSettings.copy(sortBy = newSortBy)
        searchSettings = newSettings
        _searchSettingsFlow.value = searchSettings
    }

    fun resetAllSettings() {
        Log.d(APPLICATION_TAG, "All settings were reset")
        val newSettings = searchSettings.copy(
            type = Type.ALL,
            country = null,
            genre = null,
            yearFrom = SEARCH_MIN_YEAR,
            yearTo = currentYear,
            ratingFrom = SEARCH_MIN_RATING,
            ratingTo = SEARCH_MAX_RATING,
            sortBy = Order.NUM_VOTE,
            showOnlyUnwatched = false
        )
        searchSettings = newSettings
        _searchSettingsFlow.value = searchSettings
    }

    fun getGenresAndCountriesFiltered(filter: String? = null) {
        if (genresAndCountriesForFiltering == null) return
        val initialCountryList = genresAndCountriesForFiltering!!.countries
        val initialGenreList = genresAndCountriesForFiltering!!.genres
        val result = genresAndCountriesForFiltering?.copy(
            countries = initialCountryList.filter { it.value.contains(filter ?: "", true) },
            genres = initialGenreList.filter { it.value.contains(filter ?: "", true) }
        )
        _genresAndCountriesForFilteringFlow.value = result
    }

    fun selectYear(year: Int, type: ChoosingYearFragmentStateAdapter.Type) {
        when (type) {
            ChoosingYearFragmentStateAdapter.Type.SEARCH_FROM -> {
                _preliminaryChosenYearFromLiveData.value = year
            }
            ChoosingYearFragmentStateAdapter.Type.SEARCH_TO -> {
                _preliminaryChosenYearToLiveData.value = year
            }
        }
    }

    fun resetPreliminaryYearSelection() {
        if (searchSettings.yearTo == currentYear && searchSettings.yearFrom == SEARCH_MIN_YEAR) {
            _preliminaryChosenYearFromLiveData.value = null
            _preliminaryChosenYearToLiveData.value = null
        } else {
            _preliminaryChosenYearFromLiveData.value = searchSettings.yearFrom
            _preliminaryChosenYearToLiveData.value = searchSettings.yearTo
        }
    }

    fun setDefaultYearRange() {
        _preliminaryChosenYearFromLiveData.value = null
        _preliminaryChosenYearToLiveData.value = null
        changeSearchSettingsYearRange(SEARCH_MIN_YEAR, currentYear)
    }

    fun getGenresAndCountriesForFiltering() {
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                genresAndCountriesForFiltering = getGenresAndCountriesForFilteringUseCase.execute()
                _genresAndCountriesForFilteringFlow.value = genresAndCountriesForFiltering
            }
        }
    }

    fun enableSearching() {
        isSearchingEnabled = true
    }

    fun disableSearching() {
        isSearchingEnabled = false
    }

    fun enableScrollingUp() {
        _isScrollingUpEnabled.value = true
    }

    fun disableScrollingUp() {
        _isScrollingUpEnabled.value = false
    }

    private fun defineCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        return calendar.get(Calendar.YEAR)
    }

    fun clearErrorState(){
        _connectionErrorState.value = false
        _otherErrorState.value = false
    }
}