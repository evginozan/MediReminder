package com.evginozan.medireminder.domain.usecase

import com.evginozan.medireminder.domain.model.BloodSugar
import com.evginozan.medireminder.domain.repository.BloodSugarRepository
import kotlinx.coroutines.flow.Flow

class GetBloodSugarRecordByIdUseCase(
    private val repository: BloodSugarRepository
) {
    suspend operator fun invoke(id: Long): BloodSugar? {
        return repository.getRecordById(id)
    }
}