package com.evginozan.medireminder.presentation.bloodpressure

import com.evginozan.medireminder.domain.model.BloodPressure
import java.time.LocalDate

data class BloodPressureUiState(
    val recordsByDate: Map<LocalDate, List<BloodPressure>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val validationError: String? = null
)