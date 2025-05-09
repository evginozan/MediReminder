package com.evginozan.medireminder.data.local.dao

import androidx.room.*
import com.evginozan.medireminder.data.local.entity.BloodSugarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodSugarDao {
    @Query("SELECT * FROM blood_sugar_records ORDER BY date DESC, id DESC")
    fun getAllRecords(): Flow<List<BloodSugarEntity>>

    @Query("SELECT * FROM blood_sugar_records WHERE id = :id")
    suspend fun getRecordById(id: Long): BloodSugarEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BloodSugarEntity): Long

    @Update
    suspend fun updateRecord(record: BloodSugarEntity)

    @Delete
    suspend fun deleteRecord(record: BloodSugarEntity)
}