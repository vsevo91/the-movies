package com.example.domain.entities.movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MoviePremiere(
    override val id: Int,
    override val nameRu: String?,
    override val nameEn: String?,
    override val imageSmall: String,
    override val imageLarge: String,
    override val genres: List<String>,
    override val rating: String?,
    override val nameOriginal: String?
) : MovieGeneral(), Parcelable