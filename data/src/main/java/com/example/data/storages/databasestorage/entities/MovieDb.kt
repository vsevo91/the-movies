package com.example.data.storages.databasestorage.entities

import androidx.room.*

@Entity
data class MovieDb(
    @PrimaryKey val movieId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val imageSmall: String,
    val imageLarge: String,
    val rating: String?,
    val nameOriginal: String?,
    val coverUrl: String?,
    val logoUrl: String?,
    val reviewsCount: Int,
    val ratingAwait: Double?,
    val year: Int?,
    val filmLength: Int?,
    val slogan: String?,
    val description: String?,
    val shortDescription: String?,
    val startYear: Int?,
    val endYear: Int?,
    val serial: Boolean?,
    val completed: Boolean?,
    val ratingAgeLimits: String?
)
