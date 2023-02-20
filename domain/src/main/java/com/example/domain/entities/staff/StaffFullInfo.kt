package com.example.domain.entities.staff

import android.os.Parcelable
import com.example.domain.entities.movie.MovieRelatedToStaff
import kotlinx.parcelize.Parcelize

@Parcelize
data class StaffFullInfo(
    val personId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val posterUrl: String,
    val profession: String?,
    val movies: List<MovieRelatedToStaff>
) : Parcelable