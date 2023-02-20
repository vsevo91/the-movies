package com.example.domain.usecases

import com.example.domain.entities.filtering.Order
import com.example.domain.entities.filtering.Type
import com.example.domain.entities.movie.MovieByFiltersListPaged
import com.example.domain.repositories.MovieFilteredRepository
import javax.inject.Inject

class GetMovieListFilteredUseCase @Inject constructor(
    private val movieFilteredRepository: MovieFilteredRepository
) {
    suspend fun execute(
        countries: Array<Int>?,
        genres: Array<Int>?,
        order: Order,
        type: Type,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int,
        yearTo: Int,
        keyword: String,
        page: Int
    ): MovieByFiltersListPaged {
        return movieFilteredRepository.getMovieListFiltered(
            countries,
            genres,
            order.value,
            type.value,
            ratingFrom,
            ratingTo,
            yearFrom,
            yearTo,
            keyword,
            page
        )
    }
}