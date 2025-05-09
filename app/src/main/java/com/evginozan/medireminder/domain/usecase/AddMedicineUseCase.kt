package com.evginozan.medireminder.domain.usecase

import com.evginozan.medireminder.domain.model.Medicine
import com.evginozan.medireminder.domain.repository.MedicineRepository

class AddMedicineUseCase(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(medicine: Medicine): Long {
        return repository.insertMedicine(medicine)
    }
}