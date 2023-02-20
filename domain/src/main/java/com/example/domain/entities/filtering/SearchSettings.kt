package com.example.domain.entities.filtering

data class SearchSettings(
    val type: Type,
    val country: CountryForFiltering?,
    val genre: GenreForFiltering?,
    val yearFrom: Int,
    val yearTo: Int,
    val ratingFrom: Int,
    val ratingTo: Int,
    val sortBy: Order,
    val keyword: String,
    val showOnlyUnwatched: Boolean
)
