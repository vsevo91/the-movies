package com.example.domain.usecases

import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.repositories.UserCollectionsRepository
import javax.inject.Inject

class DeleteMovieFromUserCollectionUseCase @Inject constructor(
    private val userCollectionsRepository: UserCollectionsRepository
) {
    suspend fun execute(movieId: Int, userCollectionId: Int) {
        userCollectionsRepository.deleteMovieFromUserCollection(movieId, userCollectionId)
    }
}