package com.example.domain.entities.filtering

data class GenresAndCountriesForFiltering(
    val genres: List<GenreForFiltering>,
    val countries: List<CountryForFiltering>
)