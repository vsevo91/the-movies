package com.example.data.repositories

import android.util.Log
import com.example.data.storages.ApiStaffRelatedToMovieStorage
import com.example.data.storages.apistorage.entities.staff.StaffRelatedToMovieResponse
import com.example.domain.entities.staff.StaffRelatedToMovie
import com.example.domain.repositories.StaffRelatedToMovieRepository
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_RECONNECT_ATTEMPTS
import com.example.domain.utilities.REPOSITORY_ERRORS_TAG
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject

class StaffRelatedToMovieRepositoryImpl @Inject constructor(
    private val apiStaffRelatedToMovieStorage: ApiStaffRelatedToMovieStorage
) : StaffRelatedToMovieRepository {

    private var attemptCounter = 1

    override suspend fun getStaffByMovieId(movieId: Int): List<StaffRelatedToMovie> {
        val response = apiStaffRelatedToMovieStorage.getStaffById(movieId)
        return if (response.isSuccessful) {
            if (attemptCounter > 1) Log.d(
                APPLICATION_TAG + REPOSITORY_ERRORS_TAG,
                "Reconnected successfully. Attempts: $attemptCounter"
            )
            attemptCounter = 1
            response.body()!!.mapToStaffList()
        } else {
            val message = makeErrorMessage(response)
            Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
            if(response.code() == 404) return emptyList()
            if (response.code() == 429) {
                attemptCounter++
                if (attemptCounter < MAX_RECONNECT_ATTEMPTS) {
                    delay(calculateDelay(attemptCounter))
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, "Reconnecting...")
                    getStaffByMovieId(movieId)
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
            appendLine("Unsuccessful request form \"${this@StaffRelatedToMovieRepositoryImpl.javaClass.name}\"")
            appendLine("Error code: ${response.code()}")
            appendLine("Error string: ${response.errorBody()?.string()}")
        }
    }

    private fun List<StaffRelatedToMovieResponse>.mapToStaffList(): List<StaffRelatedToMovie> {
        val list = mutableListOf<StaffRelatedToMovie>()
        this.forEach {
            val item = StaffRelatedToMovie(
                staffId = it.staffId,
                nameRu = it.nameRu,
                nameEn = it.nameEn,
                description = it.description,
                posterUrl = it.posterUrl,
                professionKey = it.professionKey
            )
            list.add(item)
        }
        return list
    }
}