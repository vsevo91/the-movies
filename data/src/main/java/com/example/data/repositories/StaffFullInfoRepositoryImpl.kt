package com.example.data.repositories

import android.util.Log
import com.example.data.storages.ApiStaffFullInfoStorage
import com.example.data.storages.apistorage.entities.staff.StaffFullInfoResponse
import com.example.domain.entities.movie.MovieRelatedToStaff
import com.example.domain.entities.staff.StaffFullInfo
import com.example.domain.repositories.StaffFullInfoRepository
import com.example.domain.utilities.APPLICATION_TAG
import com.example.domain.utilities.MAX_RECONNECT_ATTEMPTS
import com.example.domain.utilities.REPOSITORY_ERRORS_TAG
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject

class StaffFullInfoRepositoryImpl @Inject constructor(
    private val apiStaffFullInfoStorage: ApiStaffFullInfoStorage
) : StaffFullInfoRepository {

    private var attemptCounter = 1

    override suspend fun getStaffByStaffId(staffId: Int): StaffFullInfo {
        val response = apiStaffFullInfoStorage.getStaffFullInfoById(staffId)
        return if (response.isSuccessful) {
            if (attemptCounter > 1) Log.d(
                APPLICATION_TAG + REPOSITORY_ERRORS_TAG,
                "Reconnected successfully. Attempts: $attemptCounter"
            )
            attemptCounter = 1
            response.body()!!.mapToStaffFullInfo()
        } else {
            val message = makeErrorMessage(response)
            Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, message)
            if (response.code() == 429) {
                attemptCounter++
                if (attemptCounter < MAX_RECONNECT_ATTEMPTS) {
                    delay(calculateDelay(attemptCounter))
                    Log.d(APPLICATION_TAG + REPOSITORY_ERRORS_TAG, "Reconnecting...")
                    getStaffByStaffId(staffId)
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
            appendLine("Unsuccessful request form \"${this@StaffFullInfoRepositoryImpl.javaClass.name}\"")
            appendLine("Error code: ${response.code()}")
            appendLine("Error string: ${response.errorBody()?.string()}")
        }
    }

    private fun StaffFullInfoResponse.mapToStaffFullInfo(): StaffFullInfo {
        val listOfMovies = mutableListOf<MovieRelatedToStaff>()
        this.films.forEach {
            val movie = MovieRelatedToStaff(
                id = it.filmId,
                nameRu = it.nameRu,
                nameEn = it.nameEn,
                nameOriginal = null,
                rating = it.rating,
                description = it.description,
                professionKey = it.professionKey
            )
            listOfMovies.add(movie)
        }
        return StaffFullInfo(
            personId = this.personId,
            nameRu = this.nameRu,
            nameEn = this.nameEn,
            posterUrl = this.posterUrl,
            profession = this.profession,
            movies = listOfMovies
        )
    }
}