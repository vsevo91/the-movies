package com.example.domain.entities.movie

data class MovieFromCollection(
    override val id: Int,
    override val nameRu: String?,
    override val nameEn: String?,
    override val imageSmall: String,
    override val imageLarge: String,
    override val genres: List<String>?,
    override val rating: String?,
    override val nameOriginal: String?,
    val coverUrl: String?,
    val logoUrl: String?,
    val reviewsCount: Int,
    val ratingAwait: Double?,
    val year: Int?,
    val filmLength: Int?,
    val slogan: String?,
    val description: String?,
    val shortDescription: String?,
    val countries: List<String>,
    val startYear: Int?,
    val endYear: Int?,
    val serial: Boolean?,
    val completed: Boolean?,
    val ratingAgeLimits: String?,
    val addingTime: Long
) : MovieGeneral()