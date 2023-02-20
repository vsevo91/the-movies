package com.example.data.storages.databasestorage.entities

import androidx.room.Entity

@Entity(primaryKeys = ["movieId", "value"])
data class MovieCountryCrossRefDb(
    val movieId: Int,
    val value: String
)
