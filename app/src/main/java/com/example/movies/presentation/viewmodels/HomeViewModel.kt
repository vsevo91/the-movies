package com.example.movies.presentation.viewmodels

import com.example.domain.entities.filtering.GenresAndCountriesForFiltering
import android.util.Log
import androidx.lifecycle.*
import com.example.domain.entities.filtering.Order
import com.example.domain.entities.filtering.Type
import com.example.domain.entities.movie.*
import com.example.domain.usecases.*
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.WATCHED_COLLECTION_ID
import com.example.movies.presentation.extensions.makeQuerySafely
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPremieresListUseCase: GetPremieresListUseCase,
    private val getMovieFullInfoByIdUseCase: GetMovieFullInfoByIdUseCase,
    private val getMovieTopListByTypeUseCase: GetMovieTopListByTypeUseCase,
    private val getMovieListFilteredUseCase: GetMovieListFilteredUseCase,
    private val getGenresAndCountriesForFilteringUseCase: GetGenresAndCountriesForFilteringUseCase,
    getUserCollectionsUseCase: GetUserCollectionsUseCase
) : ViewModel() {

    private var genresAndCountriesForFiltering: GenresAndCountriesForFiltering? = null
    private var isMovieSentForDynamicCollection1 = false
    private var isMovieSentForDynamicCollection2 = false

    private val watchedMovies = getUserCollectionsUseCase.execute().map { userCollections ->
        Log.d(APPLICATION_TAG, "WatchedMovies received")
        userCollections.firstOrNull { userCollection ->
            userCollection.id == WATCHED_COLLECTION_ID
        }?.movies
    }

    private val _connectionErrorState = MutableLiveData(false)
    val connectionErrorState: LiveData<Boolean> = _connectionErrorState

    private val _otherErrorState = MutableLiveData(false)
    val otherErrorState: LiveData<Boolean> = _otherErrorState

    private val _premieresList = MutableLiveData<List<MoviePremiere>>()
    val combinedPremieresList = combineAndCheckIfWatched(_premieresList.asFlow())

    private val _popularMovieList = MutableLiveData<List<MovieTop>>()
    val combinedPopularMovieList = combineAndCheckIfWatched(_popularMovieList.asFlow())

    private val _movieListCollection1 = MutableLiveData<List<MovieByFilters>>()
    val combinedMovieListCollection1 = combineAndCheckIfWatched(_movieListCollection1.asFlow())

    private val _movieListCollection2 = MutableLiveData<List<MovieByFilters>>()
    val combinedMovieListCollection2 = combineAndCheckIfWatched(_movieListCollection2.asFlow())

    private val _titleForDynamicCollection1 = MutableLiveData<String>()
    val titleForDynamicCollection1: LiveData<String> get() = _titleForDynamicCollection1

    private val _titleForDynamicCollection2 = MutableLiveData<String>()
    val titleForDynamicCollection2: LiveData<String> get() = _titleForDynamicCollection2

    private val _countryIdAndGenreIdForDynamicCollection1 = MutableLiveData<List<Int>>()
    val countryIdAndGenreIdForDynamicCollection1: LiveData<List<Int>> get() = _countryIdAndGenreIdForDynamicCollection1

    private val _countryIdAndGenreIdForDynamicCollection2 = MutableLiveData<List<Int>>()
    val countryIdAndGenreIdForDynamicCollection2: LiveData<List<Int>> get() = _countryIdAndGenreIdForDynamicCollection2

    private val _movieTop250List = MutableLiveData<List<MovieTop>>()
    val combinedMovieTop250List = combineAndCheckIfWatched(_movieTop250List.asFlow())

    private val _series = MutableLiveData<List<MovieByFilters>>()
    val combinedSeries = combineAndCheckIfWatched(_series.asFlow())

    fun getPremieresListForPreview() {
        val year = defineCurrentYear()
        val month = defineCurrentMonth()
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val list = getPremieresListUseCase.execute(year, month)
                _premieresList.value = list
            }
        }
    }


    fun getPopularMovieListForPreview() {
        val pageForPreview = 1
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val listPaged =
                    getMovieTopListByTypeUseCase.execute(
                        TOP_100_POPULAR_FILMS,
                        pageForPreview
                    )
                val resultList = if (listPaged.totalPages > 1) {
                    val dummy = MovieTop(
                        id = 0,
                        nameRu = null,
                        nameEn = null,
                        imageSmall = "",
                        imageLarge = "",
                        genres = emptyList(),
                        rating = null,
                        nameOriginal = null,
                        countries = emptyList(),
                        year = null
                    )
                    val list = listPaged.films.toMutableList()
                    list.add(dummy)
                    list
                } else {
                    listPaged.films
                }
                _popularMovieList.value = resultList
            }
        }
    }

    fun getMovieForDynamicCollection1() {
        if (isMovieSentForDynamicCollection1) {
            return
        } else {
            val country = TopCountries.values().random().nameRu
            val genre = TopGenres.values().random().nameRu
            val title = "$genre $country"
            _titleForDynamicCollection1.value = title
            val pageForPreview = 1
            viewModelScope.launch {
                makeQuerySafely(_connectionErrorState, _otherErrorState) {
                    val countryId = defineCountryId(country)
                    val genreId = defineGenreId(genre)
                    _countryIdAndGenreIdForDynamicCollection1.value = listOf(countryId, genreId)
                    val countriesArray = arrayOf(countryId)
                    val genresArray = arrayOf(genreId)
                    val listPaged = getMovieListFilteredUseCase.execute(
                        countries = countriesArray,
                        genres = genresArray,
                        order = Order.RATING,
                        type = Type.ALL,
                        ratingFrom = 8,
                        ratingTo = 10,
                        yearFrom = 1000,
                        yearTo = 3000,
                        keyword = "",
                        page = pageForPreview
                    )
                    val resultList = if (listPaged.totalPages > 1) {
                        val dummy = MovieByFilters(
                            id = 0,
                            nameRu = null,
                            nameEn = null,
                            imageSmall = "",
                            imageLarge = "",
                            genres = emptyList(),
                            rating = null,
                            nameOriginal = null,
                            countries = emptyList(),
                            year = null,
                            type = ""
                        )
                        val list = listPaged.films.toMutableList()
                        list.add(dummy)
                        list
                    } else {
                        listPaged.films
                    }
                    _movieListCollection1.value = resultList
                    isMovieSentForDynamicCollection1 = true
                }
            }
        }
    }

    fun getMovieForDynamicCollection2() {
        if (isMovieSentForDynamicCollection2) {
            return
        } else {
            val country = TopCountries.values().random().nameRu
            val genre = TopGenres.values().random().nameRu
            val title = "$genre $country"
            _titleForDynamicCollection2.value = title
            val pageForPreview = 1
            viewModelScope.launch {
                makeQuerySafely(_connectionErrorState, _otherErrorState) {
                    val countryId = defineCountryId(country)
                    val genreId = defineGenreId(genre)
                    _countryIdAndGenreIdForDynamicCollection2.value = listOf(countryId, genreId)
                    val countriesArray = arrayOf(countryId)
                    val genresArray = arrayOf(genreId)
                    val listPaged = getMovieListFilteredUseCase.execute(
                        countries = countriesArray,
                        genres = genresArray,
                        order = Order.RATING,
                        type = Type.ALL,
                        ratingFrom = 8,
                        ratingTo = 10,
                        yearFrom = 1000,
                        yearTo = 3000,
                        keyword = "",
                        page = pageForPreview
                    )
                    val resultList = if (listPaged.totalPages > 1) {
                        val dummy = MovieByFilters(
                            id = 0,
                            nameRu = null,
                            nameEn = null,
                            imageSmall = "",
                            imageLarge = "",
                            genres = emptyList(),
                            rating = null,
                            nameOriginal = null,
                            countries = emptyList(),
                            year = null,
                            type = ""
                        )
                        val list = listPaged.films.toMutableList()
                        list.add(dummy)
                        list

                    } else {
                        listPaged.films
                    }
                    _movieListCollection2.value = resultList
                    isMovieSentForDynamicCollection2 = true
                }
            }
        }

    }

    fun getTop250Movies() {
        val pageForPreview = 1
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val listPaged =
                    getMovieTopListByTypeUseCase.execute(
                        TOP_250_BEST_FILMS,
                        pageForPreview
                    )
                val resultList = if (listPaged.totalPages > 1) {
                    val dummy = MovieTop(
                        id = 0,
                        nameRu = null,
                        nameEn = null,
                        imageSmall = "",
                        imageLarge = "",
                        genres = emptyList(),
                        rating = null,
                        nameOriginal = null,
                        countries = emptyList(),
                        year = null
                    )
                    val list = listPaged.films.toMutableList()
                    list.add(dummy)
                    list
                } else {
                    listPaged.films
                }
                _movieTop250List.value = resultList
            }
        }

    }

    fun getTVSeries() {
        val pageForPreview = 1
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState) {
                val listPaged = getMovieListFilteredUseCase.execute(
                    countries = null,
                    genres = null,
                    order = Order.NUM_VOTE,
                    type = Type.TV_SERIES,
                    ratingFrom = 8,
                    ratingTo = 10,
                    yearFrom = 1000,
                    yearTo = 3000,
                    keyword = "",
                    page = pageForPreview
                )
                val resultList = if (listPaged.totalPages > 1) {
                    val dummy = MovieByFilters(
                        id = 0,
                        nameRu = null,
                        nameEn = null,
                        imageSmall = "",
                        imageLarge = "",
                        genres = emptyList(),
                        rating = null,
                        nameOriginal = null,
                        countries = emptyList(),
                        year = null,
                        type = ""
                    )
                    val list = listPaged.films.toMutableList()
                    list.add(dummy)
                    list
                } else {
                    listPaged.films
                }
                _series.value = resultList
            }
        }

    }

    private suspend fun defineCountryId(countryName: String): Int {
        return if (genresAndCountriesForFiltering != null) {
            genresAndCountriesForFiltering!!.countries.first { it.value.contains(countryName) }.id
        } else {
            val result: Deferred<GenresAndCountriesForFiltering> = viewModelScope.async {
                genresAndCountriesForFiltering =
                    getGenresAndCountriesForFilteringUseCase.execute()
                genresAndCountriesForFiltering!!
            }
            result.await().countries.first { it.value.contains(countryName) }.id
        }
    }

    private suspend fun defineGenreId(genreName: String): Int {
        return if (genresAndCountriesForFiltering != null) {
            genresAndCountriesForFiltering!!.genres.first { it.value.contains(genreName) }.id
        } else {
            val result: Deferred<GenresAndCountriesForFiltering> = viewModelScope.async {

                genresAndCountriesForFiltering =
                    getGenresAndCountriesForFilteringUseCase.execute()
                genresAndCountriesForFiltering!!

            }
            result.await().genres.first { it.value.contains(genreName) }.id
        }

    }

    fun getMovieFullInfo(movieId: Int): Flow<MovieFullInfo> {
        val movieChannel = Channel<MovieFullInfo>()
        val movieFlow = movieChannel.receiveAsFlow()
        viewModelScope.launch {
            makeQuerySafely(
                _connectionErrorState,
                _otherErrorState,
                onError = { movieChannel.cancel() }) {
                val movie = getMovieFullInfoByIdUseCase.execute(movieId)
                movieChannel.send(movie!!)
                movieChannel.cancel()
            }
        }
        return movieFlow
    }

    private fun defineCurrentYear(): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val result = calendar.get(Calendar.YEAR)
        Log.d(APPLICATION_TAG, "Current year: $result")
        return result
    }

    private fun defineCurrentMonth(): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val result = when (calendar.get(Calendar.MONTH)) {
            0 -> "JANUARY"
            1 -> "FEBRUARY"
            2 -> "MARCH"
            3 -> "APRIL"
            4 -> "MAY"
            5 -> "JUNE"
            6 -> "JULY"
            7 -> "AUGUST"
            8 -> "SEPTEMBER"
            9 -> "OCTOBER"
            10 -> "NOVEMBER"
            11 -> "DECEMBER"
            else -> throw IllegalStateException()
        }
        Log.d(APPLICATION_TAG, "Current month:$result")
        return result
    }

    private fun combineAndCheckIfWatched(flow1: Flow<List<MovieGeneral>>): LiveData<List<MovieGeneral>> {
        val flow2 = watchedMovies
        return combine(flow1, flow2) { moviesFromFlow1, watchedMovies ->
            moviesFromFlow1.map { movie ->
                val isWatched = watchedMovies?.firstOrNull { it.id == movie.id } != null
                movie.also {
                    it.isWatched = isWatched
                }
            }
        }.asLiveData(viewModelScope.coroutineContext)
    }

    fun clearErrorState(){
        _connectionErrorState.value = false
        _otherErrorState.value = false
    }

    companion object {
        private const val TOP_100_POPULAR_FILMS = "TOP_100_POPULAR_FILMS"
        private const val TOP_250_BEST_FILMS = "TOP_250_BEST_FILMS"

        enum class TopCountries(val nameRu: String) {
            USA("США"),
            FRANCE("Франция"),
            POLAND("Польша"),
            UK("Великобритания"),
            SWEDEN("Швеция"),
            INDIA("Индия"),
            SPAIN("Испания"),
            GERMANY("Германия"),
            ITALY("Италия"),
            JAPAN("Япония"),
            CHINA("Китай"),
            DENMARK("Дания"),
            RUSSIA("Россия")
        }

        enum class TopGenres(val nameRu: String) {
            THRILLER("триллер"),
            DRAMA("драма"),
            CRIMINAL("криминал"),
            MELODRAMA("мелодрама"),
            DETECTIVE("детектив"),
            FICTION("фантастика"),
            ADVENTURE("приключения"),
            ACTION("боевик"),
            FANTASY("фэнтези"),
            COMEDY("комедия"),
            HORROR("ужасы"),
            CARTOON("мультфильм"),
            FAMILY("семейный"),
            ANIME("аниме"),
            CHILDREN("детский")
        }
    }
}
