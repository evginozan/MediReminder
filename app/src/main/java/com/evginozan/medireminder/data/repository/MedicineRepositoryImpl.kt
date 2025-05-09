package com.evginozan.medireminder.data.repository

import com.evginozan.medireminder.data.local.dao.MedicineDao
import com.evginozan.medireminder.data.mapper.toDomain
import com.evginozan.medireminder.data.mapper.toEntity
import com.evginozan.medireminder.domain.model.Medicine
import com.evginozan.medireminder.domain.repository.MedicineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MedicineRepositoryImpl(
    private val medicineDao: MedicineDao
) : MedicineRepository
{

    override fun getAllMedicines(): Flow<List<Medicine>> {
        return medicineDao.getAllMedicines().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMedicineById(id: Long): Medicine? {
        return medicineDao.getMedicineById(id)?.toDomain()
    }

    override suspend fun insertMedicine(medicine: Medicine): Long {
        return medicineDao.insertMedicine(medicine.toEntity())
    }

    override suspend fun updateMedicine(medicine: Medicine) {
        medicineDao.updateMedicine(medicine.toEntity())
    }

    override suspend fun deleteMedicine(medicine: Medicine) {
        medicineDao.deleteMedicine(medicine.toEntity())
    }

    override fun getLowStockMedicines(threshold: Int): Flow<List<Medicine>> {
        return medicineDao.getLowStockMedicines(threshold).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}