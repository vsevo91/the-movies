package com.example.domain.usecases

import androidx.paging.PagingData
import com.example.domain.entities.movie.MovieGeneral
import com.example.domain.repositories.MovieTopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieTopListAsMovieGeneralListStreamUseCase @Inject constructor(
    private val movieTopRepository: MovieTopRepository
) {
    fun execute(type: String): Flow<PagingData<MovieGeneral>> {
        return movieTopRepository.getMovieTopListByCategoryStream(type)
    }
}