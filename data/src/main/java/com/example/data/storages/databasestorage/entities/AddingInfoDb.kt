package com.example.data.storages.databasestorage.entities

import androidx.room.Entity

@Entity (primaryKeys = ["collectionId", "movieId"])
data class AddingInfoDb(
    val time: Long? = null,
    val collectionId: Int,
    val movieId:Int
)
