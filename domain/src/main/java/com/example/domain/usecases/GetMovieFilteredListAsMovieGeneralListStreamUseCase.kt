package com.example.domain.usecases

import androidx.paging.PagingData
import com.example.domain.entities.filtering.Order
import com.example.domain.entities.filtering.Type
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.repositories.MovieFilteredRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieFilteredListAsMovieGeneralListStreamUseCase @Inject constructor(
    private val movieFilteredRepository: MovieFilteredRepository
) {
    fun execute(
        countries: Array<Int>?,
        genres: Array<Int>?,
        order: Order,
        type: Type,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int,
        yearTo: Int,
        keyword: String
    ): Flow<PagingData<MovieGeneral>> {
        return movieFilteredRepository.getMovieFilteredListAsMovieGeneralListStream(
            countries = countries,
            genres = genres,
            order = order.value,
            type = type.value,
            ratingFrom = ratingFrom,
            ratingTo = ratingTo,
            yearFrom = yearFrom,
            yearTo = yearTo,
            keyword = keyword
        )
    }
}