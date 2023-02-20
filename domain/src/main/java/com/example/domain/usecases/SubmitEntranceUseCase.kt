package com.example.domain.usecases

import com.example.domain.repositories.UserCollectionsRepository
import javax.inject.Inject

class SubmitEntranceUseCase @Inject constructor(
    private val userCollectionsRepository: UserCollectionsRepository
) {
    suspend fun execute() {
        userCollectionsRepository.submitEntrance()
    }
}