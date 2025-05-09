package com.evginozan.medireminder.domain.usecase

import com.evginozan.medireminder.domain.model.BloodPressure
import com.evginozan.medireminder.domain.repository.BloodPressureRepository
import kotlinx.coroutines.flow.Flow

class GetAllBloodPressureRecordsUseCase(
    private val repository: BloodPressureRepository
) {
    operator fun invoke(): Flow<List<BloodPressure>> {
        return repository.getAllRecords()
    }
}