package com.example.data.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.data.pagingsources.GeneralMoviesPagingSource
import com.example.data.storages.ApiMovieFilteredStorage
import com.example.data.storages.apistorage.entities.movie.MovieByFiltersListResponse
import com.example.domain.entities.movie.MovieByFilters
import com.example.domain.entities.movie.MovieByFiltersListPaged
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.repositories.MovieFilteredRepository
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_RECONNECT_ATTEMPTS
import com.example.domain.utilities.NETWORK_PAGE_SIZE
import com.example.domain.utilities.REPOSITORY_ERRORS_TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class MovieFilteredRepositoryImpl @Inject constructor(
    private val apiMovieFilteredStorage: ApiMovieFilteredStorage
) : MovieFilteredRepository {

    private var attemptCounter = 1

    override suspend fun getMovieListFiltered(
        countries: Array<Int>?,
        genres: Array<Int>?,
        order: String,
        type: String,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int,
        yearTo: Int,
        keyword: String,
        page: Int
    ): MovieByFiltersListPaged {
        val response = apiMovieFilteredStorage.getMovieFilteredPaged(
            countries,
            genres,
            order,
            type,
            ratingFrom,
            ratingTo,
            yearFrom,
            yearTo,
            keyword,
            page
        )
        return if (response.isSuccessful) {
            if (attemptCounter > 1) Log.d(
                APPLICATION_TAG + REPOSITORY_ERRORS_TAG,
                "Reconnected successfully. Attempts: $attemptCounter"
            )
            attemptCounter = 1
            response.body()!!.mapToMovieByFiltersListPaged()
        } else {
            val message = makeErrorMessage(response)
            Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
            if (response.code() == 429) {
                attemptCounter++
                if (attemptCounter < MAX_RECONNECT_ATTEMPTS) {
                    delay(calculateDelay(attemptCounter))
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, "Reconnecting...")
                    getMovieListFiltered(
                        countries,
                        genres,
                        order,
                        type,
                        ratingFrom,
                        ratingTo,
                        yearFrom,
                        yearTo,
                        keyword,
                        page
                    )
                } else {
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
                    throw Throwable(message = response.message())
                }
            } else {
                Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
                throw Throwable(message = response.message())
            }
        }
    }

    private fun calculateDelay(counter: Int): Long {
        val delay = counter / 10 * 100
        Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, delay.toString())
        return delay.toLong()
    }

    private fun makeErrorMessage(response: Response<*>): String {
        return buildString {
            appendLine("Unsuccessful request form \"${this@MovieFilteredRepositoryImpl.javaClass.name}\"")
            appendLine("Error code: ${response.code()}")
            appendLine("Error string: ${response.errorBody()?.string()}")
        }
    }

    override fun getMovieFilteredListAsMovieGeneralListStream(
        countries: Array<Int>?,
        genres: Array<Int>?,
        order: String,
        type: String,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int,
        yearTo: Int,
        keyword: String
    ): Flow<PagingData<MovieGeneral>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = true, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = {
                GeneralMoviesPagingSource { page ->
                    getMovieListFiltered(
                        countries,
                        genres,
                        order,
                        type,
                        ratingFrom,
                        ratingTo,
                        yearFrom,
                        yearTo,
                        keyword,
                        page
                    )
                }
            }
        ).flow
    }

    private fun MovieByFiltersListResponse.mapToMovieByFiltersListPaged(): MovieByFiltersListPaged {
        val movies = mutableListOf<MovieByFilters>()
        this.items.forEach { movieByFiltersResponse ->
            val movie = MovieByFilters(
                id = movieByFiltersResponse.kinopoiskId,
                nameRu = movieByFiltersResponse.nameRu,
                nameEn = movieByFiltersResponse.nameEn,
                imageSmall = movieByFiltersResponse.posterUrlPreview,
                imageLarge = movieByFiltersResponse.posterUrl,
                genres = movieByFiltersResponse.genres.map { it.genre },
                rating = movieByFiltersResponse.ratingKinopoisk?.toString(),
                nameOriginal = movieByFiltersResponse.nameOriginal,
                countries = movieByFiltersResponse.countries.map { it.country },
                year = movieByFiltersResponse.year,
                type = movieByFiltersResponse.type
            )
            movies.add(movie)
        }
        return MovieByFiltersListPaged(
            total = this.total,
            totalPages = this.totalPages,
            films = movies,
        )
    }

}



