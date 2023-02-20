package com.example.data.storages.databasestorage.entities

import androidx.room.*


data class UserCollectionWithMoviesWithGenreAndCountryDb(
    @Embedded
    val userCollection: UserCollectionDb,

    @Relation(
        parentColumn = "collectionId",
        entityColumn = "movieId",
        associateBy = Junction(CollectionMovieCrossRefDb::class)
    )
    val movies: List<MovieWithGenreAndCountryDb>,

    @Relation(
        AddingInfoDb::class,
        parentColumn = "collectionId",
        entityColumn = "collectionId"
    )
    val addingInfo: List<AddingInfoDb>
)