package com.example.data.storages

import com.example.data.storages.apistorage.entities.staff.StaffFullInfoResponse
import retrofit2.Response

interface ApiStaffFullInfoStorage {
    suspend fun getStaffFullInfoById(staffId: Int): Response<com.example.data.storages.apistorage.entities.staff.StaffFullInfoResponse>
}