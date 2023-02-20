package com.example.domain.entities.series

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Series(
    val total: Int,
    val items: List<Season>
) : Parcelable