package com.example.domain.entities.gallery

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GalleryImage(
    val imageUrl: String?,
    val previewUrl: String?
): Parcelable