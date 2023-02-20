package com.example.domain.entities.movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieRelatedToStaff(
    override val id: Int,
    override val nameRu: String?,
    override val nameEn: String?,
    override val imageSmall: String? = null,
    override val imageLarge: String? = null,
    override val genres: List<String>? = null,
    override val rating: String?,
    override val nameOriginal: String?,
    val description: String?,
    val professionKey: String
) : MovieGeneral(), Parcelable