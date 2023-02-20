package com.example.domain.repositories

import com.example.domain.entities.filtering.GenresAndCountriesForFiltering

interface GenresAndCountriesForFilteringRepository {
    suspend fun getGenresAndCountriesForFiltering(): GenresAndCountriesForFiltering
}