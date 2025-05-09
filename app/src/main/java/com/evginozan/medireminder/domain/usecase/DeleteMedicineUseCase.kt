package com.evginozan.medireminder.domain.usecase

import com.evginozan.medireminder.domain.model.Medicine
import com.evginozan.medireminder.domain.repository.MedicineRepository

class DeleteMedicineUseCase(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(medicine: Medicine) {
        repository.deleteMedicine(medicine)
    }
}