package com.example.data.storages.databasestorage.entities

import androidx.room.*

@Entity
data class UserCollectionDb(
    @PrimaryKey val collectionId: Int? = null,
    val name: String,
    val icon: Int,
    val canBeDeleted: Boolean,
    val isHidden: Boolean
)
//{
//    @PrimaryKey(autoGenerate = true)
//    var collectionId: Int = 0
//}