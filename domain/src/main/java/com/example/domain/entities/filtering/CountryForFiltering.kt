package com.example.domain.entities.filtering

data class CountryForFiltering(
    override val id: Int,
    override val value: String
) : ParameterForFiltering()