package com.example.domain.usecases

import android.util.Log
import com.example.domain.repositories.UserCollectionsRepository
import com.example.domain.utilities.APPLICATION_TAG
import javax.inject.Inject

class GetIfShowOnboardingScreenUseCase @Inject constructor(
    private val userCollectionsRepository: UserCollectionsRepository
) {
    suspend fun execute(): Boolean {
        val isEntered = userCollectionsRepository.getIfEntranceWasCommitted()
        Log.d(APPLICATION_TAG, "Entrance was committed: $isEntered")
        return !isEntered
    }
}