package com.evginozan.medireminder.data.repository

import com.evginozan.medireminder.data.local.dao.BloodPressureDao
import com.evginozan.medireminder.data.local.entity.BloodPressureEntity
import com.evginozan.medireminder.data.mapper.toBloodPressure
import com.evginozan.medireminder.data.mapper.toEntity
import com.evginozan.medireminder.domain.model.BloodPressure
import com.evginozan.medireminder.domain.repository.BloodPressureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BloodPressureRepositoryImpl(
    private val bloodPressureDao: BloodPressureDao
) : BloodPressureRepository {

    override fun getAllRecords(): Flow<List<BloodPressure>> {
        return bloodPressureDao.getAllRecords().map { entities ->
            entities.map { it.toBloodPressure() }
        }
    }

    override suspend fun getRecordById(id: Long): BloodPressure? {
        return bloodPressureDao.getRecordById(id)?.toBloodPressure()
    }

    override suspend fun insertRecord(record: BloodPressure): Long {
        return bloodPressureDao.insertRecord(record.toEntity())
    }

    override suspend fun updateRecord(record: BloodPressure) {
        bloodPressureDao.updateRecord(record.toEntity())
    }

    override suspend fun deleteRecord(record: BloodPressure) {
        bloodPressureDao.deleteRecord(record.toEntity())
    }
}
