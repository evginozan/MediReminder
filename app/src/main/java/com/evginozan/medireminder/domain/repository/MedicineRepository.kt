package com.evginozan.medireminder.domain.repository

import com.evginozan.medireminder.domain.model.Medicine
import kotlinx.coroutines.flow.Flow

interface MedicineRepository {
    fun getAllMedicines(): Flow<List<Medicine>>
    suspend fun getMedicineById(id: Long): Medicine?
    suspend fun insertMedicine(medicine: Medicine): Long
    suspend fun updateMedicine(medicine: Medicine)
    suspend fun deleteMedicine(medicine: Medicine)
    fun getLowStockMedicines(threshold: Int): Flow<List<Medicine>>
}