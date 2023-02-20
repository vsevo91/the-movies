package com.example.domain.entities.movie

data class MovieSimilar(
    override val id: Int,
    override val nameRu: String?,
    override val nameEn: String?,
    override val imageLarge: String,
    override val imageSmall: String,
    override val genres: List<String>?,
    override val rating: String? = null,
    override val nameOriginal: String?,
) : MovieGeneral()