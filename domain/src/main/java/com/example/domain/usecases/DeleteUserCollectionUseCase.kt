package com.example.domain.usecases

import com.example.domain.entities.UserCollection
import com.example.domain.repositories.UserCollectionsRepository
import javax.inject.Inject

class DeleteUserCollectionUseCase @Inject constructor(
    private val userCollectionsRepository: UserCollectionsRepository
) {
    suspend fun execute(userCollection: UserCollection): Boolean {
        return try {
            userCollectionsRepository.deleteUserCollection(userCollection)
            return true
        } catch (t: Throwable) {
            false
        }
    }
}