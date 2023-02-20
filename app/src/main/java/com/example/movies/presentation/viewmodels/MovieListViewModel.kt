package com.example.movies.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.domain.entities.filtering.Order
import com.example.domain.entities.filtering.Type
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.usecases.GetMovieFilteredListAsMovieGeneralListStreamUseCase
import com.example.domain.usecases.GetMovieFullInfoByIdUseCase
import com.example.domain.usecases.GetMovieTopListAsMovieGeneralListStreamUseCase
import com.example.domain.usecases.GetUserCollectionsUseCase
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.WATCHED_COLLECTION_ID
import com.example.movies.presentation.extensions.makeQuerySafely
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val getMovieFullInfoByIdUseCase: GetMovieFullInfoByIdUseCase,
    private val getMovieTopListAsMovieGeneralListStreamUseCase: GetMovieTopListAsMovieGeneralListStreamUseCase,
    private val getMovieFilteredListAsMovieGeneralListStreamUseCase: GetMovieFilteredListAsMovieGeneralListStreamUseCase,
    getUserCollectionsUseCase: GetUserCollectionsUseCase
) : ViewModel() {

    private val _connectionErrorState = MutableLiveData(false)
    val connectionErrorState: LiveData<Boolean> = _connectionErrorState

    private val _otherErrorState = MutableLiveData(false)
    val otherErrorState: LiveData<Boolean> = _otherErrorState

    private var _stream: Flow<PagingData<MovieGeneral>>? = null
    private val stream get() = _stream!!
    val watchedMovies = getUserCollectionsUseCase.execute().map { userCollections ->
        userCollections.first { userCollection ->
            userCollection.id == WATCHED_COLLECTION_ID
        }.movies
    }
    private var _combinedFlow: Flow<PagingData<MovieGeneral>>? = null
    val combinedFlow get() = _combinedFlow!!

    fun getMovieTopStream(type: String) {
        if (_stream == null) {
            _stream = getMovieTopListAsMovieGeneralListStreamUseCase.execute(type)
                .cachedIn(viewModelScope)
            _combinedFlow = combine(stream, watchedMovies) { moviesFromStream, watchedMovies ->
                moviesFromStream.map { movie ->
                    val isWatched = watchedMovies.firstOrNull { it.id == movie.id } != null
                    movie.also {
                        it.isWatched = isWatched
                    }
                }
            }
        }
    }

    fun getMovieFilteredStream(
        countryId: Int? = null,
        genreId: Int? = null,
        orderType: Order = Order.RATING,
        type: Type = Type.ALL
    ) {
        if (_stream == null) {
            val countriesArray = if (countryId != null) arrayOf(countryId) else null
            val genresArray = if (genreId != null) arrayOf(genreId) else null
            _stream = getMovieFilteredListAsMovieGeneralListStreamUseCase.execute(
                countries = countriesArray,
                genres = genresArray,
                order = orderType,
                type = type,
                ratingFrom = 8,
                ratingTo = 10,
                yearFrom = 1000,
                yearTo = 3000,
                keyword = ""
            ).cachedIn(viewModelScope)
            _combinedFlow = combine(stream, watchedMovies) { moviesFromStream, watchedMovies ->
                moviesFromStream.map { movie ->
                    val isWatched = watchedMovies.firstOrNull { it.id == movie.id } != null
                    Log.d(APPLICATION_TAG, "${movie.id} is watched: $isWatched")
                    movie.also {
                        it.isWatched = isWatched
                    }
                }
            }
        }
    }

    fun getMovieFullInfoSource(movieId: Int): Flow<MovieFullInfo?> {
        val result = Channel<MovieFullInfo?>()
        val movieFlow = result.receiveAsFlow()
        viewModelScope.launch {
            makeQuerySafely(_connectionErrorState, _otherErrorState){
                Log.d(APPLICATION_TAG, "$movieId: SEND")
                val movieFullInfo = getMovieFullInfoByIdUseCase.execute(movieId)
                result.send(movieFullInfo)
                result.cancel()
            }
        }
        return movieFlow
    }

    fun clearErrorState(){
        _connectionErrorState.value = false
        _otherErrorState.value = false
    }
}