package com.example.domain.entities.movie

abstract class MovieGeneral {
    abstract val id: Int?
    abstract val nameRu: String?
    abstract val nameEn: String?
    abstract val nameOriginal: String?
    abstract val imageSmall: String?
    abstract val imageLarge: String?
    abstract val genres: List<String>?
    abstract val rating: String?
    var isWatched = false
}