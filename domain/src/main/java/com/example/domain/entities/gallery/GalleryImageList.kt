package com.example.domain.entities.gallery

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryImageList(
    val items: List<GalleryImage>
) : Parcelable