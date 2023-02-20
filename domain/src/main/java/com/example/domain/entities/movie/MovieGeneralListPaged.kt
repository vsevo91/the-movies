package com.example.domain.entities.movie

abstract class MovieGeneralListPaged{
    abstract val totalPages: Int
    abstract val films: List<MovieGeneral>
}
