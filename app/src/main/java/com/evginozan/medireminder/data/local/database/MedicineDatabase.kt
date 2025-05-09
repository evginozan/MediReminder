package com.evginozan.medireminder.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.evginozan.medireminder.data.local.converter.DoseTimeConverter
import com.evginozan.medireminder.data.local.converter.StringListConverter
import com.evginozan.medireminder.data.local.dao.BloodPressureDao
import com.evginozan.medireminder.data.local.dao.BloodSugarDao
import com.evginozan.medireminder.data.local.dao.MedicineDao
import com.evginozan.medireminder.data.local.entity.BloodPressureEntity
import com.evginozan.medireminder.data.local.entity.BloodSugarEntity
import com.evginozan.medireminder.data.local.entity.MedicineEntity

@Database(
    entities = [
        MedicineEntity::class,
        BloodPressureEntity::class,
        BloodSugarEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(DoseTimeConverter::class, StringListConverter::class)
abstract class MedicineDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun bloodPressureDao(): BloodPressureDao
    abstract fun bloodSugarDao(): BloodSugarDao

    companion object {
        private const val DATABASE_NAME = "medicine_database"

        @Volatile
        private var INSTANCE: MedicineDatabase? = null

        fun getInstance(context: Context): MedicineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedicineDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}