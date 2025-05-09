package com.evginozan.medireminder.presentation.home

import com.evginozan.medireminder.domain.model.Medicine

data class HomeUiState(
    val medicines: List<Medicine> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)