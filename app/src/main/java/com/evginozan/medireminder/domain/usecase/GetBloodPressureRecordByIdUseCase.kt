package com.evginozan.medireminder.domain.usecase

import com.evginozan.medireminder.domain.model.BloodPressure
import com.evginozan.medireminder.domain.repository.BloodPressureRepository

class GetBloodPressureRecordByIdUseCase(
    private val repository: BloodPressureRepository
) {
    suspend operator fun invoke(id: Long): BloodPressure? {
        return repository.getRecordById(id)
    }
}