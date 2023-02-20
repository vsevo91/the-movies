package com.example.domain.usecases

import com.example.domain.entities.UserCollection
import com.example.domain.repositories.UserCollectionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserCollectionsUseCase @Inject constructor(
    private val userCollectionsRepository: UserCollectionsRepository
) {

    fun execute(): Flow<List<UserCollection>> {
        return userCollectionsRepository.getUserCollections()
    }
}