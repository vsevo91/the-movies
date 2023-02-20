package com.example.domain.usecases

import com.example.domain.entities.staff.StaffFullInfo
import com.example.domain.repositories.StaffFullInfoRepository
import javax.inject.Inject

class GetStaffFullInfoByStaffIdUseCase @Inject constructor(
    private val staffFullInfoRepository: StaffFullInfoRepository
) {
    suspend fun execute(staffId: Int): StaffFullInfo {
        return staffFullInfoRepository.getStaffByStaffId(staffId)
    }
}