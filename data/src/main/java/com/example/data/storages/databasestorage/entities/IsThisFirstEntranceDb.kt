package com.example.data.storages.databasestorage.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IsThisFirstEntranceDb(
    @PrimaryKey val id: Int = 1,
    val value: Boolean
)