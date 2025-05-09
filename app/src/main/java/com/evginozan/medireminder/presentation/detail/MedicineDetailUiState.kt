package com.evginozan.medireminder.presentation.detail

import com.evginozan.medireminder.domain.model.Medicine

data class MedicineDetailState(
    val medicine: Medicine? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)