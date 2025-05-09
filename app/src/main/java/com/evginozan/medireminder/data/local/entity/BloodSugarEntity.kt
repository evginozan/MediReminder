package com.evginozan.medireminder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_sugar_records")
data class BloodSugarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val value: Int, // Şeker değeri
    val date: Long, // Tarih - Epoch millis
    val timeOfDay: TimeOfDay, // Sabah, öğle, ikindi, akşam
    val isFasting: Boolean? = null, // Aç/tok durumu
    val note: String? = null
)