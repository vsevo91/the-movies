package com.example.data.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.data.pagingsources.GeneralMoviesPagingSource
import com.example.data.storages.ApiMovieTopStorage
import com.example.data.storages.apistorage.entities.movie.MovieTopListResponse
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.entities.movie.MovieTop
import com.example.domain.entities.movie.MovieTopListPaged
import com.example.domain.repositories.MovieTopRepository
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_RECONNECT_ATTEMPTS
import com.example.domain.utilities.NETWORK_PAGE_SIZE
import com.example.domain.utilities.REPOSITORY_ERRORS_TAG
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class MovieTopRepositoryImpl @Inject constructor(
    private val apiMovieTopStorage: ApiMovieTopStorage
) : MovieTopRepository {

    private var attemptCounter = 1

    override suspend fun getMovieTopListByTypeAndPage(type: String, page: Int): MovieTopListPaged {
        val response = apiMovieTopStorage.getMovieTopByIdPaged(type, page)
        return if (response.isSuccessful) {
            if (attemptCounter > 1) Log.d(
                APPLICATION_TAG + REPOSITORY_ERRORS_TAG,
                "Reconnected successfully. Attempts: $attemptCounter"
            )
            attemptCounter = 1
            response.body()!!.mapToMovieTopListPaged()
        } else {
            val message = makeErrorMessage(response)
            Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
            if (response.code() == 429) {
                attemptCounter++
                if (attemptCounter < MAX_RECONNECT_ATTEMPTS) {
                    delay(calculateDelay(attemptCounter))
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, "Reconnecting...")
                    getMovieTopListByTypeAndPage(type, page)
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
            appendLine("Unsuccessful request form \"${this@MovieTopRepositoryImpl.javaClass.name}\"")
            appendLine("Error code: ${response.code()}")
            appendLine("Error string: ${response.errorBody()?.string()}")
        }
    }

    override fun getMovieTopListByCategoryStream(type: String): Flow<PagingData<MovieGeneral>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = true, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = {
                GeneralMoviesPagingSource { page ->
                    getMovieTopListByTypeAndPage(type, page)
                }
            }
        ).flow
    }

    private fun MovieTopListResponse.mapToMovieTopListPaged(): MovieTopListPaged {
        val films = mutableListOf<MovieTop>()
        this.films.forEach { movieTopResponse ->
            val movie = MovieTop(
                id = movieTopResponse.filmId,
                nameRu = movieTopResponse.nameRu,
                nameEn = movieTopResponse.nameEn,
                imageSmall = movieTopResponse.posterUrlPreview,
                imageLarge = movieTopResponse.posterUrl,
                genres = movieTopResponse.genres.map { it.genre },
                rating = movieTopResponse.rating,
                year = movieTopResponse.year,
                countries = movieTopResponse.countries.map { it.country },
                nameOriginal = null
            )
            films.add(movie)
        }
        return MovieTopListPaged(
            totalPages = this.pagesCount,
            films = films
        )
    }
}



