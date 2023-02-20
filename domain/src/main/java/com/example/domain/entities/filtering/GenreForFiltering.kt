package com.example.domain.entities.filtering

data class GenreForFiltering(
    override val id: Int,
    override val value: String
): ParameterForFiltering()