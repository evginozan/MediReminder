package com.evginozan.medireminder.presentation.home

sealed class HomeUiEffect {
    data class ShowSnackbar(val message: String) : HomeUiEffect()
    data class NavigateToDetail(val medicineId: Long) : HomeUiEffect()
}