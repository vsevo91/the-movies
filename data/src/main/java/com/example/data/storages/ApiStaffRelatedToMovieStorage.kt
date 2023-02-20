package com.example.data.storages

import com.example.data.storages.apistorage.entities.staff.StaffRelatedToMovieResponse
import retrofit2.Response

interface ApiStaffRelatedToMovieStorage {
    suspend fun getStaffById(id: Int): Response<List<StaffRelatedToMovieResponse>>
}