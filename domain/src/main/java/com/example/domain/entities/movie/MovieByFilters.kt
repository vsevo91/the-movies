package com.example.domain.entities.movie

data class MovieByFilters(
    override val id: Int,
    override val nameRu: String?,
    override val nameEn: String?,
    override val imageSmall: String,
    override val imageLarge: String,
    override val genres: List<String>,
    override val rating: String?,
    override val nameOriginal: String?,
    val countries: List<String>,
    val year: Int?,
    val type: String
) : MovieGeneral()