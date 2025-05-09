package com.evginozan.medireminder.presentation.home

import com.evginozan.medireminder.domain.model.Medicine
import java.time.LocalTime

sealed class HomeUiEvent {
    data object LoadMedicines : HomeUiEvent()
    data class DeleteMedicine(val medicine: Medicine) : HomeUiEvent()
    data class AddMedicine(
        val name: String,
        val totalCount: Int,
        val dailyDoseCount: Int,
        val doseTimes: List<DoseTimeInput>,
        val imageUri: String?,
        val notes: String?
    ) : HomeUiEvent()

    data class NavigateToDetail(val medicineId: Long) : HomeUiEvent()
}

data class DoseTimeInput(
    val time: LocalTime,
    val relation: MealRelationInput
)

enum class MealRelationInput {
    BEFORE_MEAL,
    AFTER_MEAL,
    WITH_MEAL,
    ANY
}