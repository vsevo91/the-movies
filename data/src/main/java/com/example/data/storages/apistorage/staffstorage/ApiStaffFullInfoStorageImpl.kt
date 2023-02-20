package com.example.data.storages.apistorage.staffstorage

import com.example.data.storages.ApiStaffFullInfoStorage
import com.example.data.storages.apistorage.KinopoiskService
import com.example.data.storages.apistorage.entities.staff.StaffFullInfoResponse
import retrofit2.Response
import javax.inject.Inject

class ApiStaffFullInfoStorageImpl @Inject constructor(
    private val kinopoiskService: KinopoiskService
) : ApiStaffFullInfoStorage {
    override suspend fun getStaffFullInfoById(staffId: Int): Response<com.example.data.storages.apistorage.entities.staff.StaffFullInfoResponse> {
        return kinopoiskService.getStaffFullInfoById(staffId)
    }
}