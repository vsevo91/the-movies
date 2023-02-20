package com.example.data.repositories

import android.util.Log
import com.example.data.storages.ApiSimilarMoviesStorage
import com.example.data.storages.apistorage.entities.movie.SimilarMovieListResponse
import com.example.domain.entities.movie.MovieSimilar
import com.example.domain.repositories.SimilarMovieRepository
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_RECONNECT_ATTEMPTS
import com.example.domain.utilities.REPOSITORY_ERRORS_TAG
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject

class SimilarMovieRepositoryImpl @Inject constructor(
    private val apiSimilarMoviesStorage: ApiSimilarMoviesStorage
) : SimilarMovieRepository {

    private var attemptCounter = 1

    override suspend fun getSimilarMovieById(movieId: Int): List<MovieSimilar> {
        val response = apiSimilarMoviesStorage.getSimilarMoviesById(movieId)
        return if (response.isSuccessful) {
            if (attemptCounter > 1) Log.d(
                APPLICATION_TAG + REPOSITORY_ERRORS_TAG,
                "Reconnected successfully. Attempts: $attemptCounter"
            )
            attemptCounter = 1
            response.body()!!.mapToListOfMovieSimilar()
        } else {
            val message = makeErrorMessage(response)
            Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
            if (response.code() == 429) {
                attemptCounter++
                if (attemptCounter < MAX_RECONNECT_ATTEMPTS) {
                    delay(calculateDelay(attemptCounter))
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, "Reconnecting...")
                    getSimilarMovieById(movieId)
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
            appendLine("Unsuccessful request form \"${this@SimilarMovieRepositoryImpl.javaClass.name}\"")
            appendLine("Error code: ${response.code()}")
            appendLine("Error string: ${response.errorBody()?.string()}")
        }
    }

    private fun SimilarMovieListResponse.mapToListOfMovieSimilar(): List<MovieSimilar> {
        val list = mutableListOf<MovieSimilar>()
        this.items.forEach {
            val newItem = MovieSimilar(
                id = it.filmId,
                nameRu = it.nameRu,
                nameEn = it.nameEn,
                imageLarge = it.posterUrl,
                imageSmall = it.posterUrlPreview,
                genres = null,
                nameOriginal = it.nameOriginal
            )
            list.add(newItem)
        }
        return list
    }
}
















