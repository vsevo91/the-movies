package com.example.data.repositories

import android.util.Log
import com.example.data.storages.ApiGenresAndCountriesForFilteringStorage
import com.example.data.storages.apistorage.entities.filtering.GenresAndCountriesForFilteringResponse
import com.example.domain.entities.filtering.CountryForFiltering
import com.example.domain.entities.filtering.GenreForFiltering
import com.example.domain.entities.filtering.GenresAndCountriesForFiltering
import com.example.domain.repositories.GenresAndCountriesForFilteringRepository
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_RECONNECT_ATTEMPTS
import com.example.domain.utilities.REPOSITORY_ERRORS_TAG
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject

class GenresAndCountriesForFilteringRepositoryImpl @Inject constructor(
    private val apiGenresAndCountriesForFilteringStorage: ApiGenresAndCountriesForFilteringStorage
) : GenresAndCountriesForFilteringRepository {

    private var attemptCounter = 1

    override suspend fun getGenresAndCountriesForFiltering(): GenresAndCountriesForFiltering {
        val response = apiGenresAndCountriesForFilteringStorage.getGenresAndCountriesForFiltering()
        return if (response.isSuccessful) {
            if (attemptCounter > 1) Log.d(
                APPLICATION_TAG + REPOSITORY_ERRORS_TAG,
                "Reconnected successfully. Attempts: $attemptCounter"
            )
            attemptCounter = 1
            response.body()!!.mapToGenresAndCountriesForFiltering()
        } else {
            val message = makeErrorMessage(response)
            Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
            if (response.code() == 429) {
                attemptCounter++
                if (attemptCounter < MAX_RECONNECT_ATTEMPTS) {
                    delay(calculateDelay(attemptCounter))
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, "Reconnecting...")
                    getGenresAndCountriesForFiltering()
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
            appendLine("Unsuccessful request form \"${this@GenresAndCountriesForFilteringRepositoryImpl.javaClass.name}\"")
            appendLine("Error code: ${response.code()}")
            appendLine("Error string: ${response.errorBody()?.string()}")
        }
    }

    private fun GenresAndCountriesForFilteringResponse.mapToGenresAndCountriesForFiltering(): GenresAndCountriesForFiltering {
        val genres = mutableListOf<GenreForFiltering>()
        val countries = mutableListOf<CountryForFiltering>()
        this.genres.forEach {
            val genre = GenreForFiltering(
                id = it.id,
                value = it.genre
            )
            genres.add(genre)
        }
        this.countries.forEach {
            val country = CountryForFiltering(
                id = it.id,
                value = it.country
            )
            countries.add(country)
        }
        return GenresAndCountriesForFiltering(
            genres = genres,
            countries = countries
        )
    }
}



