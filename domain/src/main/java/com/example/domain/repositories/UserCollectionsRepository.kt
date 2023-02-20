package com.example.domain.repositories

import com.example.domain.entities.UserCollection
import com.example.domain.entities.movie.MovieFullInfo
import kotlinx.coroutines.flow.Flow


interface UserCollectionsRepository {
    fun getUserCollections(): Flow<List<UserCollection>>

    suspend fun addUserCollection(userCollection: UserCollection)

    suspend fun deleteUserCollection(userCollection: UserCollection)

    suspend fun addMovieToUserCollection(
        movieFullInfo: MovieFullInfo,
        userCollectionId: Int
    )

    suspend fun deleteMovieFromUserCollection(
        movieId: Int,
        userCollectionId: Int
    )

    suspend fun getIfEntranceWasCommitted(): Boolean

    suspend fun submitEntrance()
}