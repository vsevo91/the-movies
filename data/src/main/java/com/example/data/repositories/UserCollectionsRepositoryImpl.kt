package com.example.data.repositories

import com.example.data.storages.DbUserCollectionStorage
import com.example.data.storages.databasestorage.entities.*
import com.example.domain.entities.UserCollection
import com.example.domain.entities.movie.MovieFromCollection
import com.example.domain.entities.movie.MovieFullInfo
import com.example.domain.repositories.UserCollectionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserCollectionsRepositoryImpl @Inject constructor(
    private val dbUserCollectionStorage: DbUserCollectionStorage
) : UserCollectionsRepository {

    override fun getUserCollections(): Flow<List<UserCollection>> {
        return dbUserCollectionStorage.getUserCollection().map { it.mapToListOfUserCollection() }
    }

    override suspend fun addUserCollection(userCollection: UserCollection) {
        dbUserCollectionStorage.addUserCollection(userCollection.mapToUserCollectionDb())
    }

    override suspend fun deleteUserCollection(userCollection: UserCollection) {
        dbUserCollectionStorage.deleteUserCollection(userCollection.mapToUserCollectionDb())
    }

    override suspend fun addMovieToUserCollection(
        movieFullInfo: MovieFullInfo, userCollectionId: Int
    ) {
        dbUserCollectionStorage.addMovieToUserCollection(
            movieDb = movieFullInfo.mapToMovieDb(),
            genresDb = movieFullInfo.mapToGenresDb(),
            countriesDb = movieFullInfo.mapToCountriesDb(),
            userCollectionId = userCollectionId
        )
    }

    override suspend fun deleteMovieFromUserCollection(movieId: Int, userCollectionId: Int) {
        dbUserCollectionStorage.deleteMovieFromUserCollection(movieId, userCollectionId)
    }

    override suspend fun getIfEntranceWasCommitted(): Boolean {
        return dbUserCollectionStorage.getIfEntranceWasCommitted()
    }

    override suspend fun submitEntrance() {
        dbUserCollectionStorage.submitEntrance()
    }

    private fun List<UserCollectionWithMoviesWithGenreAndCountryDb>.mapToListOfUserCollection(): List<UserCollection> {
        val resultList = mutableListOf<UserCollection>()
        this.forEach { userCollectionFullInfoDb ->
            val item = UserCollection(
                id = userCollectionFullInfoDb.userCollection.collectionId,
                name = userCollectionFullInfoDb.userCollection.name,
                icon = userCollectionFullInfoDb.userCollection.icon,
                movies = userCollectionFullInfoDb.movies.map {
                    it.mapToMovieFullInfo(
                        userCollectionFullInfoDb
                    )
                },
                canBeDeleted = userCollectionFullInfoDb.userCollection.canBeDeleted,
                isHidden = userCollectionFullInfoDb.userCollection.isHidden
            )
            resultList.add(item)
        }
        return resultList
    }

    private fun MovieWithGenreAndCountryDb.mapToMovieFullInfo(collection: UserCollectionWithMoviesWithGenreAndCountryDb): MovieFromCollection {
        val listOfAddingInfo = collection.addingInfo
        val currentAddingInfo = listOfAddingInfo.first{it.movieId == this.movie.movieId}
        val currentTime = currentAddingInfo.time!!
        return MovieFromCollection(
            id = this.movie.movieId,
            completed = this.movie.completed,
            countries = this.countries.map { it.value },
            coverUrl = this.movie.coverUrl,
            description = this.movie.description,
            endYear = this.movie.endYear,
            filmLength = this.movie.filmLength,
            genres = this.genres?.map { it.value },
            imageLarge = this.movie.imageLarge,
            imageSmall = this.movie.imageSmall,
            logoUrl = this.movie.logoUrl,
            nameEn = this.movie.nameEn,
            nameOriginal = this.movie.nameOriginal,
            nameRu = this.movie.nameRu,
            rating = this.movie.rating,
            ratingAgeLimits = this.movie.ratingAgeLimits,
            ratingAwait = this.movie.ratingAwait,
            reviewsCount = this.movie.reviewsCount,
            serial = this.movie.serial,
            shortDescription = this.movie.shortDescription,
            slogan = this.movie.slogan,
            startYear = this.movie.startYear,
            year = this.movie.year,
            addingTime = currentTime
        )
    }

    private fun UserCollection.mapToUserCollectionDb(): UserCollectionDb {
        val moviesDb = mutableListOf<MovieDb>()
        this.movies.forEach {
            val movieDb = it.mapToMovieDb()
            moviesDb.add(movieDb)
        }
        return UserCollectionDb(
            collectionId = id,
            name = name,
            icon = icon,
            canBeDeleted = canBeDeleted,
            isHidden = isHidden
        )
    }

    private fun MovieFullInfo.mapToMovieDb(): MovieDb {
        val countriesDb = mutableListOf<CountryDb>()
        val genresDb = mutableListOf<GenreDb>()
        this.countries.forEach {
            val countryDb = CountryDb(value = it)
            countriesDb.add(countryDb)
        }
        this.genres?.forEach {
            val genreDb = GenreDb(value = it)
            genresDb.add(genreDb)
        }
        return MovieDb(
            completed = this.completed,
            coverUrl = this.coverUrl,
            description = this.description,
            endYear = this.endYear,
            filmLength = this.filmLength,
            imageLarge = this.imageLarge,
            imageSmall = this.imageSmall,
            logoUrl = this.logoUrl,
            movieId = this.id,
            nameEn = this.nameEn,
            nameOriginal = this.nameOriginal,
            nameRu = this.nameRu,
            rating = this.rating,
            ratingAgeLimits = this.ratingAgeLimits,
            ratingAwait = this.ratingAwait,
            reviewsCount = this.reviewsCount,
            serial = this.serial,
            shortDescription = this.shortDescription,
            slogan = this.slogan,
            startYear = this.startYear,
            year = this.year,
        )
    }

    private fun MovieFromCollection.mapToMovieDb(): MovieDb {
        val countriesDb = mutableListOf<CountryDb>()
        val genresDb = mutableListOf<GenreDb>()
        this.countries.forEach {
            val countryDb = CountryDb(value = it)
            countriesDb.add(countryDb)
        }
        this.genres?.forEach {
            val genreDb = GenreDb(value = it)
            genresDb.add(genreDb)
        }
        return MovieDb(
            completed = this.completed,
            coverUrl = this.coverUrl,
            description = this.description,
            endYear = this.endYear,
            filmLength = this.filmLength,
            imageLarge = this.imageLarge,
            imageSmall = this.imageSmall,
            logoUrl = this.logoUrl,
            movieId = this.id,
            nameEn = this.nameEn,
            nameOriginal = this.nameOriginal,
            nameRu = this.nameRu,
            rating = this.rating,
            ratingAgeLimits = this.ratingAgeLimits,
            ratingAwait = this.ratingAwait,
            reviewsCount = this.reviewsCount,
            serial = this.serial,
            shortDescription = this.shortDescription,
            slogan = this.slogan,
            startYear = this.startYear,
            year = this.year,
        )
    }

    private fun MovieFullInfo.mapToGenresDb(): List<GenreDb>? {
        val resultList = mutableListOf<GenreDb>()
        this.genres?.forEach {
            val genreDb = GenreDb(value = it)
            resultList.add(genreDb)
        }
        return resultList.ifEmpty { null }
    }

    private fun MovieFullInfo.mapToCountriesDb(): List<CountryDb>? {
        val resultList = mutableListOf<CountryDb>()
        this.countries.forEach {
            val countryDb = CountryDb(value = it)
            resultList.add(countryDb)
        }
        return resultList.ifEmpty { null }
    }
}


