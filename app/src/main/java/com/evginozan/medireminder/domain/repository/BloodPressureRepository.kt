package com.evginozan.medireminder.domain.repository

import com.evginozan.medireminder.domain.model.BloodPressure
import kotlinx.coroutines.flow.Flow

interface BloodPressureRepository {
    fun getAllRecords(): Flow<List<BloodPressure>>
    suspend fun getRecordById(id: Long): BloodPressure?
    suspend fun insertRecord(record: BloodPressure): Long
    suspend fun updateRecord(record: BloodPressure)
    suspend fun deleteRecord(record: BloodPressure)
}