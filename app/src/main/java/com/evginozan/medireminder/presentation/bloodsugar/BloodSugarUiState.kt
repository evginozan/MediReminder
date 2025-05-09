package com.evginozan.medireminder.presentation.bloodsugar

import com.evginozan.medireminder.domain.model.BloodSugar
import java.time.LocalDate

data class BloodSugarUiState(
    val recordsByDate: Map<LocalDate, List<BloodSugar>> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val validationError: String? = null
)