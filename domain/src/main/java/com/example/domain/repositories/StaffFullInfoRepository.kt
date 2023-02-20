package com.example.domain.repositories

import com.example.domain.entities.staff.StaffFullInfo

interface StaffFullInfoRepository {
    suspend fun getStaffByStaffId(staffId: Int): StaffFullInfo
}