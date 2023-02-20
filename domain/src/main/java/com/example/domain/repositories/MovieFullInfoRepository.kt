package com.example.domain.repositories

import com.example.domain.entities.movie.MovieFullInfo

interface MovieFullInfoRepository {
    suspend fun getMovieById(id: Int): MovieFullInfo?
}