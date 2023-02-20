package com.example.data.storages.databasestorage

import androidx.room.*
import com.example.data.storages.databasestorage.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCollectionDao {
    @Transaction
    @Query("SELECT * FROM UserCollectionDb")
    fun getAll(): Flow<List<UserCollectionWithMoviesWithGenreAndCountryDb>>

    @Insert
    suspend fun addUserCollection(userCollectionDb: UserCollectionDb)

    @Insert
    suspend fun addAllUserCollections(listOfUserCollectionsDb: List<UserCollectionDb>)

    @Delete
    suspend fun deleteUserCollection(userCollectionDb: UserCollectionDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovie(movieDb: MovieDb)

    @Query("DELETE FROM MovieDb WHERE MovieDb.movieId = :movieId")
    suspend fun deleteMovie(movieId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGenre(genreDb: GenreDb)

    @Delete
    suspend fun deleteGenre(genreDb: GenreDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCountry(countryDb: CountryDb)

    @Delete
    suspend fun deleteCountry(countryDb: CountryDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGenreToMovie(movieGenreCrossRefDb: MovieGenreCrossRefDb)

    @Query("DELETE FROM MovieGenreCrossRefDb WHERE MovieGenreCrossRefDb.movieId = :movieId")
    suspend fun deleteGenreToMovie(movieId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCountryToMovie(movieCountryCrossRefDb: MovieCountryCrossRefDb)

    @Query("DELETE FROM MovieCountryCrossRefDb WHERE MovieCountryCrossRefDb.movieId = :movieId")
    suspend fun deleteCountryToMovie(movieId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovieToCollection(collectionToCountryDb: CollectionMovieCrossRefDb)

    @Delete
    suspend fun deleteMovieFromCollection(collectionToCountryDb: CollectionMovieCrossRefDb)

    @Query("SELECT * FROM CollectionMovieCrossRefDb WHERE CollectionMovieCrossRefDb.movieId = :movieId")
    suspend fun getCollectionsByMovie(movieId: Int): List<CollectionMovieCrossRefDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMovieAddingInfo(addingInfoDb: AddingInfoDb)

    @Delete
    suspend fun deleteMovieAddingInfo(addingInfoDb: AddingInfoDb)

    @Query("DELETE FROM CollectionMovieCrossRefDb WHERE CollectionMovieCrossRefDb.collectionId = :collectionId")
    suspend fun deleteAllMoviesByCollection(collectionId: Int)

    @Query("DELETE FROM AddingInfoDb WHERE AddingInfoDb.collectionId = :collectionId")
    suspend fun deleteAllAddingInfoByCollection(collectionId: Int)

    @Query("SELECT * FROM CollectionMovieCrossRefDb WHERE CollectionMovieCrossRefDb.collectionId = :collectionId")
    suspend fun getAllMovieIdsByCollectionId(collectionId: Int): List<CollectionMovieCrossRefDb>

    @Query("SELECT * FROM CollectionMovieCrossRefDb")
    suspend fun getAllCollectionMovieCrossRefDb(): List<CollectionMovieCrossRefDb>

    @Transaction
    suspend fun addMovieFullInfoToUserCollection(
        movieDb: MovieDb,
        genresDb: List<GenreDb>?,
        countriesDb: List<CountryDb>?,
        userCollectionId: Int
    ) {
        val time = System.currentTimeMillis()
        val addingInfo = AddingInfoDb(
            time = time,
            collectionId = userCollectionId,
            movieId = movieDb.movieId
        )
        addMovieAddingInfo(addingInfo)
        addMovie(movieDb)
        countriesDb?.forEach {
            val movieCountryCrossRefDb =
                MovieCountryCrossRefDb(movieId = movieDb.movieId, it.value)
            addCountry(it)
            addCountryToMovie(movieCountryCrossRefDb)
        }
        genresDb?.forEach {
            val movieGenreCrossRefDb =
                MovieGenreCrossRefDb(movieId = movieDb.movieId, it.value)
            addGenre(it)
            addGenreToMovie(movieGenreCrossRefDb)
        }
        val collectionToMovie = CollectionMovieCrossRefDb(userCollectionId, movieDb.movieId)
        addMovieToCollection(collectionToMovie)
    }

    @Transaction
    suspend fun deleteMovieFromUserCollection(movieId: Int, userCollectionId: Int) {
        val collectionToMovie = CollectionMovieCrossRefDb(userCollectionId, movieId)
        deleteMovieFromCollection(collectionToMovie)
        val addingInfo = AddingInfoDb(collectionId = userCollectionId, movieId = movieId)
        deleteMovieAddingInfo(addingInfo)
        val collectionIds = getCollectionsByMovie(movieId)
        if (collectionIds.isEmpty()) {
            deleteMovie(movieId)
            deleteGenreToMovie(movieId)
            deleteCountryToMovie(movieId)
        }
    }

    @Transaction
    suspend fun deleteCollection(userCollectionDb: UserCollectionDb) {
        val moviesIds = getAllMovieIdsByCollectionId(userCollectionDb.collectionId!!)
        deleteUserCollection(userCollectionDb)
        deleteAllMoviesByCollection(userCollectionDb.collectionId)
        deleteAllAddingInfoByCollection(userCollectionDb.collectionId)
        val allCrossRefsRefs = getAllCollectionMovieCrossRefDb()
        moviesIds.forEach { checkMovie ->
            if (allCrossRefsRefs.firstOrNull { it.movieId == checkMovie.movieId } == null) {
                deleteMovie(checkMovie.movieId)
                deleteGenreToMovie(checkMovie.movieId)
                deleteCountryToMovie(checkMovie.movieId)
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun submitEntrance(
        isThisFirstEntranceDb: IsThisFirstEntranceDb = IsThisFirstEntranceDb(
            value = true
        )
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun noEntranceWasCommitted(
        isThisFirstEntranceDb: IsThisFirstEntranceDb = IsThisFirstEntranceDb(
            value = false
        )
    )

    @Query("SELECT * FROM IsThisFirstEntranceDb")
    suspend fun getIfEntranceWasCommitted(): List<IsThisFirstEntranceDb>
}