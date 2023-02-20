package com.example.domain.entities.movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class MovieGeneralList(
    val items: List<@RawValue MovieGeneral>
) : Parcelable