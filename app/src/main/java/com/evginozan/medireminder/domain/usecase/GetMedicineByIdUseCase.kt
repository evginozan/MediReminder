package com.evginozan.medireminder.domain.usecase

import com.evginozan.medireminder.domain.model.Medicine
import com.evginozan.medireminder.domain.repository.MedicineRepository

class GetMedicineByIdUseCase(
    private val repository: MedicineRepository
) {
    suspend operator fun invoke(id: Long): Medicine? {
        return repository.getMedicineById(id)
    }
}