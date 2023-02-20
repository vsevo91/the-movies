package com.example.domain.repositories

import com.example.domain.entities.staff.StaffRelatedToMovie

interface StaffRelatedToMovieRepository {
    suspend fun getStaffByMovieId(movieId: Int): List<StaffRelatedToMovie>
}