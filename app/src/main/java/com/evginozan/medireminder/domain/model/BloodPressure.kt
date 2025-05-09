package com.evginozan.medireminder.domain.model

import java.time.LocalDate

data class BloodPressure(
    val id: Long = 0,
    val systolic: Int, // Büyük tansiyon
    val diastolic: Int, // Küçük tansiyon
    val date: LocalDate,
    val timeOfDay: TimeOfDay,
    val isFasting: Boolean? = null,
    val note: String? = null
)