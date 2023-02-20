package com.example.domain.entities.movie

data class MovieByFiltersListPaged(
    val total: Int,
    override val totalPages: Int,
    override val films: List<MovieByFilters>
) : MovieGeneralListPaged()
