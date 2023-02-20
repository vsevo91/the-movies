package com.example.domain.entities.staff

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StaffRelatedToMovieList(
    val items: List<StaffRelatedToMovie>
) : Parcelable