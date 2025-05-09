package com.evginozan.medireminder.domain.usecase

import com.evginozan.medireminder.domain.model.BloodPressure
import com.evginozan.medireminder.domain.repository.BloodPressureRepository

class DeleteBloodPressureRecordUseCase(
    private val repository: BloodPressureRepository
) {
    suspend operator fun invoke(record: BloodPressure) {
        repository.deleteRecord(record)
    }
}