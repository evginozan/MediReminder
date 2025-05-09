package com.evginozan.medireminder.domain.repository

import com.evginozan.medireminder.domain.model.BloodSugar
import kotlinx.coroutines.flow.Flow

interface BloodSugarRepository {
    fun getAllRecords(): Flow<List<BloodSugar>>
    suspend fun getRecordById(id: Long): BloodSugar?
    suspend fun insertRecord(record: BloodSugar): Long
    suspend fun updateRecord(record: BloodSugar)
    suspend fun deleteRecord(record: BloodSugar)
}