package com.evginozan.medireminder.domain.usecase

import com.evginozan.medireminder.domain.model.Medicine
import com.evginozan.medireminder.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow

class GetAllMedicinesUseCase(
    private val repository: MedicineRepository
) {
    operator fun invoke(): Flow<List<Medicine>> {
        return repository.getAllMedicines()
    }
}