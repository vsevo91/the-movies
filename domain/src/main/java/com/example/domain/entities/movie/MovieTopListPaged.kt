package com.example.domain.entities.movie

data class MovieTopListPaged(
    override val totalPages: Int,
    override val films: List<MovieTop>
) : MovieGeneralListPaged()
