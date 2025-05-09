package com.evginozan.medireminder.data.local.dao

import androidx.room.*
import com.evginozan.medireminder.data.local.entity.BloodPressureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodPressureDao {
    @Query("SELECT * FROM blood_pressure_records ORDER BY date DESC, id DESC")
    fun getAllRecords(): Flow<List<BloodPressureEntity>>

    @Query("SELECT * FROM blood_pressure_records WHERE id = :id")
    suspend fun getRecordById(id: Long): BloodPressureEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BloodPressureEntity): Long

    @Update
    suspend fun updateRecord(record: BloodPressureEntity)

    @Delete
    suspend fun deleteRecord(record: BloodPressureEntity)
}