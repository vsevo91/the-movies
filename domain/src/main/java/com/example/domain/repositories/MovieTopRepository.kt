package com.example.domain.repositories

import androidx.paging.PagingData
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.entities.movie.MovieTop
import com.example.domain.entities.movie.MovieTopListPaged
import kotlinx.coroutines.flow.Flow

interface MovieTopRepository {

    suspend fun getMovieTopListByTypeAndPage(type: String, page: Int): MovieTopListPaged

    fun getMovieTopListByCategoryStream(type: String): Flow<PagingData<MovieGeneral>>
}