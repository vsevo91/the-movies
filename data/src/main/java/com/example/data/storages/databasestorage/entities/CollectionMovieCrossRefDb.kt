package com.example.data.storages.databasestorage.entities

import androidx.room.Entity

@Entity(primaryKeys = ["collectionId", "movieId"])
data class CollectionMovieCrossRefDb(
    val collectionId: Int,
    val movieId: Int
)
