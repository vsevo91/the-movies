package com.example.data.storages.databasestorage.entities

import androidx.room.*

@DatabaseView("SELECT * FROM MovieDb")
data class MovieWithGenreAndCountryDb(

    @Embedded
    val movie: MovieDb,

    @Relation(
        parentColumn = "movieId",
        entityColumn = "value",
        associateBy = Junction(MovieGenreCrossRefDb::class)
    )
    val genres: List<GenreDb>?,

    @Relation(
        parentColumn = "movieId",
        entityColumn = "value",
        associateBy = Junction(MovieCountryCrossRefDb::class)
    )
    val countries: List<CountryDb>,

    @Relation(
        parentColumn = "movieId",
        entityColumn = "movieId"
    )
    val addingTime: AddingInfoDb
)
