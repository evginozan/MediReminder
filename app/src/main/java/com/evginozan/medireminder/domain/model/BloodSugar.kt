package com.evginozan.medireminder.domain.model

import java.time.LocalDate

data class BloodSugar(
    val id: Long = 0,
    val value: Int,
    val date: LocalDate,
    val timeOfDay: TimeOfDay,
    val isFasting: Boolean? = null,
    val note: String? = null
)