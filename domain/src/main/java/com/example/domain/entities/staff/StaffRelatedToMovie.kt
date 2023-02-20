package com.example.domain.entities.staff

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StaffRelatedToMovie(
    val staffId: Int,
    val nameRu: String?,
    val nameEn: String?,
    val description: String?,
    val posterUrl: String?,
    val professionKey: String
) : Parcelable {
    companion object {
        const val WRITER = "WRITER"
        const val OPERATOR = "OPERATOR"
        const val EDITOR = "EDITOR"
        const val COMPOSER = "COMPOSER"
        const val PRODUCER_USSR = "PRODUCER_USSR"
        const val TRANSLATOR = "TRANSLATOR"
        const val DIRECTOR = "DIRECTOR"
        const val DESIGN = "DESIGN"
        const val PRODUCER = "PRODUCER"
        const val ACTOR = "ACTOR"
        const val VOICE_DIRECTOR = "VOICE_DIRECTOR"
        const val UNKNOWN = "UNKNOWN"
    }
}