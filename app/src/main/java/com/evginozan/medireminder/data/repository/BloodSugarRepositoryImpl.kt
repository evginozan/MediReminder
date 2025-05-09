package com.evginozan.medireminder.data.repository

import com.evginozan.medireminder.data.local.dao.BloodSugarDao
import com.evginozan.medireminder.data.mapper.toBloodSugar
import com.evginozan.medireminder.data.mapper.toEntity
import com.evginozan.medireminder.domain.model.BloodSugar
import com.evginozan.medireminder.domain.repository.BloodSugarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BloodSugarRepositoryImpl(
    private val bloodSugarDao: BloodSugarDao
) : BloodSugarRepository {

    override fun getAllRecords(): Flow<List<BloodSugar>> {
        return bloodSugarDao.getAllRecords().map { entities ->
            entities.map { it.toBloodSugar() }
        }
    }

    override suspend fun getRecordById(id: Long): BloodSugar? {
        return bloodSugarDao.getRecordById(id)?.toBloodSugar()
    }

    override suspend fun insertRecord(record: BloodSugar): Long {
        return bloodSugarDao.insertRecord(record.toEntity())
    }

    override suspend fun updateRecord(record: BloodSugar) {
        bloodSugarDao.updateRecord(record.toEntity())
    }

    override suspend fun deleteRecord(record: BloodSugar) {
        bloodSugarDao.deleteRecord(record.toEntity())
    }
}