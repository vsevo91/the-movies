package com.example.data.repositories

import android.util.Log
import com.example.data.storages.ApiPremieresStorage
import com.example.data.storages.apistorage.entities.movie.PremiereListResponse
import com.example.domain.entities.movie.MoviePremiere
import com.example.domain.repositories.PremiereRepository
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_RECONNECT_ATTEMPTS
import com.example.domain.utilities.REPOSITORY_ERRORS_TAG
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject

class PremiereRepositoryImpl @Inject constructor(
    private val apiPremieresStorage: ApiPremieresStorage
) : PremiereRepository {

    private var attemptCounter = 1

    override suspend fun getPremieresListByYearAndMonth(
        year: Int,
        month: String
    ): List<MoviePremiere> {
        val response = apiPremieresStorage.getPremieresList(year, month)
        return if (response.isSuccessful) {
            if (attemptCounter > 1) Log.d(
                APPLICATION_TAG + REPOSITORY_ERRORS_TAG,
                "Reconnected successfully. Attempts: $attemptCounter"
            )
            attemptCounter = 1
            response.body()!!.mapToPremieresList()
        } else {
            val message = makeErrorMessage(response)
            Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
            if (response.code() == 429) {
                attemptCounter++
                if (attemptCounter < MAX_RECONNECT_ATTEMPTS) {
                    delay(calculateDelay(attemptCounter))
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, "Reconnecting...")
                    getPremieresListByYearAndMonth(year, month)
                } else {
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
                    throw Exception(response.message())
                }
            } else {
                Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
                throw Exception(response.message())
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
            appendLine("Unsuccessful request form \"${this@PremiereRepositoryImpl.javaClass.name}\"")
            appendLine("Error code: ${response.code()}")
            appendLine("Error string: ${response.errorBody()?.string()}")
        }
    }

    private fun PremiereListResponse.mapToPremieresList(): List<MoviePremiere> {
        val list = mutableListOf<MoviePremiere>()
        this.items.forEach {
            val premiereLocal = MoviePremiere(
                id = it.kinopoiskId,
                nameRu = it.nameRu,
                nameEn = it.nameEn,
                imageSmall = it.posterUrlPreview,
                imageLarge = it.posterUrl,
                genres = it.genres.map { genreData ->
                    genreData.genre
                },
                nameOriginal = null,
                rating = null
            )
            list.add(premiereLocal)
        }
        return list
    }
}