package com.example.domain.repositories

import androidx.paging.PagingData
import com.example.domain.entities.movie.MovieByFiltersListPaged
import com.example.domain.entities.movie.MovieGeneral
import kotlinx.coroutines.flow.Flow

interface MovieFilteredRepository {

    suspend fun getMovieListFiltered(
        countries: Array<Int>?,
        genres: Array<Int>?,
        order: String,
        type: String,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int,
        yearTo: Int,
        keyword: String,
        page: Int
    ): MovieByFiltersListPaged

    fun getMovieFilteredListAsMovieGeneralListStream(
        countries: Array<Int>?,
        genres: Array<Int>?,
        order: String,
        type: String,
        ratingFrom: Int,
        ratingTo: Int,
        yearFrom: Int,
        yearTo: Int,
        keyword: String
    ): Flow<PagingData<MovieGeneral>>
}