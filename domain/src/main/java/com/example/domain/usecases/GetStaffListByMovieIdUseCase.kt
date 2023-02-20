package com.example.domain.usecases

import com.example.domain.entities.staff.StaffRelatedToMovie
import com.example.domain.repositories.StaffRelatedToMovieRepository
import javax.inject.Inject

class GetStaffListByMovieIdUseCase @Inject constructor(
    private val staffRelatedToMovieRepository: StaffRelatedToMovieRepository
) {
    suspend fun execute(movieId: Int): List<StaffRelatedToMovie> {
        return staffRelatedToMovieRepository.getStaffByMovieId(movieId)
    }
}