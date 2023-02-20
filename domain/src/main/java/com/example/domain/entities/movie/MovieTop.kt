package com.example.domain.entities.movie

data class MovieTop(
    override val id: Int,
    override val nameRu: String?,
    override val nameEn: String?,
    override val imageSmall: String,
    override val imageLarge: String,
    override val genres: List<String>?,
    override val rating: String?,
    override val nameOriginal: String?,
    val year: String?,
    val countries: List<String>
) : MovieGeneral()

