package com.example.data.repositories

import android.util.Log
import com.example.data.storages.ApiMovieFullInfoStorage
import com.example.data.storages.apistorage.entities.movie.MovieFullInfoResponse
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.repositories.MovieFullInfoRepository
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_RECONNECT_ATTEMPTS
import com.example.domain.utilities.REPOSITORY_ERRORS_TAG
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject

class MovieFullInfoRepositoryImpl @Inject constructor(
    private val apiMovieFullInfoStorage: ApiMovieFullInfoStorage
) : MovieFullInfoRepository {

    private var attemptCounter = 1

    override suspend fun getMovieById(id: Int): MovieFullInfo? {
        val response = apiMovieFullInfoStorage.getMovieById(id)
        return if (response.isSuccessful) {
            if (attemptCounter > 1) Log.d(
                APPLICATION_TAG + REPOSITORY_ERRORS_TAG,
                "Reconnected successfully. Attempts: $attemptCounter"
            )
            attemptCounter = 1
            response.body()!!.mapToMovieWithFullInfo()
        } else {
            val message = makeErrorMessage(response)
            Log.d(APPLICATION_TAG, "Error occurred with Movie ID: $id")
            Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
            if (response.code() == 404) {
                return null
            } else if (response.code() == 429) {
                attemptCounter++
                if (attemptCounter < MAX_RECONNECT_ATTEMPTS) {
                    delay(calculateDelay(attemptCounter))
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, "Reconnecting...")
                    getMovieById(id)
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
            appendLine("Unsuccessful request form \"${this@MovieFullInfoRepositoryImpl.javaClass.name}\"")
            appendLine("Error code: ${response.code()}")
            appendLine("Error string: ${response.errorBody()?.string()}")
        }
    }

    private fun MovieFullInfoResponse.mapToMovieWithFullInfo(): MovieFullInfo {
        val listOfCounties = mutableListOf<String>()
        val listOfGenres = mutableListOf<String>()
        this.countries.forEach {
            listOfCounties.add(it.country)
        }
        this.genres.forEach {
            listOfGenres.add(it.genre)
        }
        return MovieFullInfo(
            id = this.kinopoiskId,
            nameRu = this.nameRu,
            nameEn = this.nameEn,
            nameOriginal = this.nameOriginal,
            imageLarge = this.posterUrl,
            imageSmall = this.posterUrlPreview,
            coverUrl = this.coverUrl,
            logoUrl = this.logoUrl,
            reviewsCount = this.reviewsCount,
            rating = this.ratingKinopoisk?.toString(),
            ratingAwait = this.ratingAwait,
            year = this.year,
            filmLength = this.filmLength,
            slogan = this.slogan,
            description = this.description,
            shortDescription = this.shortDescription,
            countries = listOfCounties,
            genres = listOfGenres,
            startYear = this.startYear,
            endYear = this.endYear,
            serial = this.serial,
            completed = this.completed,
            ratingAgeLimits = this.ratingAgeLimits,
            webUrl = this.webUrl
        )
    }
}


















