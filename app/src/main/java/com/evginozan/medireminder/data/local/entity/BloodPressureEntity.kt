package com.evginozan.medireminder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_pressure_records")
data class BloodPressureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val systolic: Int, // Büyük tansiyon
    val diastolic: Int, // Küçük tansiyon
    val date: Long, // Tarih - Epoch millis
    val timeOfDay: TimeOfDay, // Sabah, öğle, ikindi, akşam
    val isFasting: Boolean? = null, // Aç/tok durumu (null: belirtilmemiş)
    val note: String? = null
)