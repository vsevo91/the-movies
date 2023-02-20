package com.example.domain.entities.series

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Episode(
    val seasonNumber: Int,
    val episodeNumber: Int,
    val nameRu: String?,
    val nameEn: String?,
    val releaseDate: String?
) : Parcelable