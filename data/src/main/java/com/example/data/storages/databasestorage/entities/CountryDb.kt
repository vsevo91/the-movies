package com.example.data.storages.databasestorage.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CountryDb(
    @PrimaryKey val value: String
)
