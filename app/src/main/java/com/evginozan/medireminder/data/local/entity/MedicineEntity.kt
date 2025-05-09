package com.evginozan.medireminder.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.evginozan.medireminder.data.local.converter.DoseTimeConverter
import kotlinx.serialization.Serializable

@Entity("medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val totalCount: Int,
    val dailyDoseCount: Int,
    @TypeConverters(DoseTimeConverter::class)
    val doseTimes: List<DoseTime>,
    val imageUri: String? = null,
    val notes: String? = null,
    val lowStockNotificationSent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class DoseTime(
    val hour: Int,
    val minute: Int,
    val relation: MealRelation = MealRelation.ANY
)

enum class MealRelation {
    BEFORE_MEAL,
    AFTER_MEAL,
    WITH_MEAL,
    ANY
}