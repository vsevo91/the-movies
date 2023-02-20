package com.example.data.repositories

import android.util.Log
import com.example.data.storages.ApiSeriesStorage
import com.example.data.storages.apistorage.entities.series.SeriesResponse
import com.example.domain.entities.series.Episode
import com.example.domain.entities.series.Season
import com.example.domain.entities.series.Series
import com.example.domain.repositories.SeriesRepository
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_RECONNECT_ATTEMPTS
import com.example.domain.utilities.REPOSITORY_ERRORS_TAG
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject

class SeriesRepositoryImpl @Inject constructor(
    private val apiSeriesStorage: ApiSeriesStorage
) : SeriesRepository {

    private var attemptCounter = 1

    override suspend fun getSeriesById(id: Int): Series {
        val response = apiSeriesStorage.getSeriesById(id)
        return if (response.isSuccessful) {
            if (attemptCounter > 1) Log.d(
                APPLICATION_TAG + REPOSITORY_ERRORS_TAG,
                "Reconnected successfully. Attempts: $attemptCounter"
            )
            attemptCounter = 1
            response.body()!!.mapToSeries()
        } else {
            val message = makeErrorMessage(response)
            Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
            if (response.code() == 429) {
                attemptCounter++
                if (attemptCounter < MAX_RECONNECT_ATTEMPTS) {
                    delay(calculateDelay(attemptCounter))
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, "Reconnecting...")
                    getSeriesById(id)
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
            appendLine("Unsuccessful request form \"${this@SeriesRepositoryImpl.javaClass.name}\"")
            appendLine("Error code: ${response.code()}")
            appendLine("Error string: ${response.errorBody()?.string()}")
        }
    }

    private fun SeriesResponse.mapToSeries(): Series {
        val listOfSeasonsFromResponse = this.items
        val listOfSeasons = mutableListOf<Season>()
        listOfSeasonsFromResponse.forEach { seasonResponse ->
            val episodesFromResponse = seasonResponse.episodes
            val episodes = mutableListOf<Episode>()
            episodesFromResponse.forEach { episodeResponse ->
                val episode = Episode(
                    seasonNumber = episodeResponse.seasonNumber,
                    episodeNumber = episodeResponse.episodeNumber,
                    nameRu = episodeResponse.nameRu,
                    nameEn = episodeResponse.nameEn,
                    releaseDate = episodeResponse.releaseDate
                )
                episodes.add(episode)
            }
            val season = Season(
                number = seasonResponse.number,
                episodes = episodes
            )
            listOfSeasons.add(season)
        }
        return Series(
            total = this.total,
            items = listOfSeasons
        )
    }

}


















