package com.evginozan.medireminder.domain.usecase

import com.evginozan.medireminder.domain.model.BloodPressure
import com.evginozan.medireminder.domain.repository.BloodPressureRepository

class UpdateBloodPressureRecordUseCase(
    private val repository: BloodPressureRepository
) {
    suspend operator fun invoke(record: BloodPressure) {
        repository.updateRecord(record)
    }
}