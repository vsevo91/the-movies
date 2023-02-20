package com.example.data.storages.databasestorage

import com.example.data.storages.DbUserCollectionStorage
import com.example.data.storages.databasestorage.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DbUserCollectionStorageImpl @Inject constructor(
    private val userCollectionDao: UserCollectionDao
) : DbUserCollectionStorage {

    override fun getUserCollection(): Flow<List<UserCollectionWithMoviesWithGenreAndCountryDb>> {
        return userCollectionDao.getAll()
    }

    override suspend fun addUserCollection(userCollectionDb: UserCollectionDb) {
        userCollectionDao.addUserCollection(userCollectionDb)
    }

    override suspend fun deleteUserCollection(userCollectionDb: UserCollectionDb) {
        userCollectionDao.deleteCollection(userCollectionDb)
    }

    override suspend fun addMovieToUserCollection(
        movieDb: MovieDb,
        genresDb: List<GenreDb>?,
        countriesDb: List<CountryDb>?,
        userCollectionId: Int
    ) {
        userCollectionDao.addMovieFullInfoToUserCollection(
            movieDb,
            genresDb,
            countriesDb,
            userCollectionId
        )
    }

    override suspend fun deleteMovieFromUserCollection(movieId: Int, userCollectionId: Int) {
        userCollectionDao.deleteMovieFromUserCollection(movieId, userCollectionId)
    }

    override suspend fun getIfEntranceWasCommitted(): Boolean {
        return userCollectionDao.getIfEntranceWasCommitted().firstOrNull()?.value ?: false
    }

    override suspend fun submitEntrance() {
        userCollectionDao.submitEntrance()
    }
}