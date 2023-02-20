package com.example.data.storages.apistorage.staffstorage

import com.example.data.storages.ApiStaffRelatedToMovieStorage
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.entities.staff.StaffRelatedToMovieResponse
import retrofit2.Response

class ApiStaffRelatedToMovieStorageImpl(
    private val kinopoiskService: KinopoiskService
) : ApiStaffRelatedToMovieStorage {
    override suspend fun getStaffById(id: Int): Response<List<com.example.data.storages.apistorage.entities.staff.StaffRelatedToMovieResponse>> {
        return kinopoiskService.getStaffById(id)
    }
}