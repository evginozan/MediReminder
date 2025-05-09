package com.evginozan.medireminder.domain.usecase

import com.evginozan.medireminder.domain.model.BloodSugar
import com.evginozan.medireminder.domain.repository.BloodSugarRepository
import kotlinx.coroutines.flow.Flow

class GetAllBloodSugarRecordsUseCase(
    private val repository: BloodSugarRepository
) {
    operator fun invoke(): Flow<List<BloodSugar>> {
        return repository.getAllRecords()
    }
}