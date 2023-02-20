package com.example.domain.usecases

import com.example.domain.entities.UserCollection
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.repositories.UserCollectionsRepository
import javax.inject.Inject

class AddMovieToUserCollectionUseCase @Inject constructor(
    private val userCollectionsRepository: UserCollectionsRepository
) {
    suspend fun execute(movieFullInfo: MovieFullInfo, userCollectionId: Int) {
        userCollectionsRepository.addMovieToUserCollection(movieFullInfo, userCollectionId)
    }
}