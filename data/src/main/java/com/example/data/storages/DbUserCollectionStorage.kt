package com.example.data.storages

import com.example.data.storages.databasestorage.entities.*
import kotlinx.coroutines.flow.Flow

interface DbUserCollectionStorage {
    fun getUserCollection(): Flow<List<UserCollectionWithMoviesWithGenreAndCountryDb>>

    suspend fun addUserCollection(userCollectionDb: UserCollectionDb)

    suspend fun deleteUserCollection(userCollectionDb: UserCollectionDb)

    suspend fun addMovieToUserCollection(
        movieDb: MovieDb,
        genresDb: List<GenreDb>?,
        countriesDb: List<CountryDb>?,
        userCollectionId: Int
    )

    suspend fun deleteMovieFromUserCollection(
        movieId: Int,
        userCollectionId: Int
    )

    suspend fun getIfEntranceWasCommitted(): Boolean

    suspend fun submitEntrance()
}