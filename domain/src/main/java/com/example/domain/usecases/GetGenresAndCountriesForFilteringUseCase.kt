package com.example.domain.usecases

import com.example.domain.entities.filtering.GenresAndCountriesForFiltering
import com.example.domain.repositories.GenresAndCountriesForFilteringRepository
import javax.inject.Inject

class GetGenresAndCountriesForFilteringUseCase @Inject constructor(
    private val genresAndCountriesForFilteringRepository: GenresAndCountriesForFilteringRepository
) {
    suspend fun execute(): GenresAndCountriesForFiltering {
        return genresAndCountriesForFilteringRepository.getGenresAndCountriesForFiltering()
    }
}