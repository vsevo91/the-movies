package com.example.domain.entities.series

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Season(
    val number: Int,
    val episodes: List<Episode>
) : Parcelable