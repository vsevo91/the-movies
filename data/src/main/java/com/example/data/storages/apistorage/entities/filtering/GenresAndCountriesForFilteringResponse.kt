package com.example.data.storages.apistorage.entities.filtering

data class GenresAndCountriesForFilteringResponse(
    val genres: List<com.example.data.storages.apistorage.entities.filtering.GenreForFilteringResponse>,
    val countries: List<com.example.data.storages.apistorage.entities.filtering.CountryForFilteringResponse>
)