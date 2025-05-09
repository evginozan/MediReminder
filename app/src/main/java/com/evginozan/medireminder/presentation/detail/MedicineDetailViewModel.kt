package com.evginozan.medireminder.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evginozan.medireminder.domain.model.DoseTime
import com.evginozan.medireminder.domain.model.Medicine
import com.evginozan.medireminder.domain.model.MealRelation
import com.evginozan.medireminder.domain.usecase.*
import com.evginozan.medireminder.presentation.home.DoseTimeInput
import com.evginozan.medireminder.presentation.home.MealRelationInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MedicineDetailViewModel(
    private val getMedicineByIdUseCase: GetMedicineByIdUseCase,
    private val updateMedicineUseCase: UpdateMedicineUseCase,
    private val deleteMedicineUseCase: DeleteMedicineUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MedicineDetailState(isLoading = true))
    val state: StateFlow<MedicineDetailState> = _state

    fun loadMedicine(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val medicine = getMedicineByIdUseCase(id)
                if (medicine != null) {
                    _state.update { it.copy(medicine = medicine, isLoading = false) }
                } else {
                    _state.update { it.copy(error = "İlaç bulunamadı", isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun updateMedicine(
        id: Long,
        name: String,
        totalCount: Int,
        dailyDoseCount: Int,
        doseTimes: List<DoseTimeInput>,
        imageUri: String?,
        notes: String?
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val medicine = Medicine(
                    id = id,
                    name = name,
                    totalCount = totalCount,
                    dailyDoseCount = dailyDoseCount,
                    doseTimes = doseTimes.map { input ->
                        DoseTime(
                            time = input.time,
                            relation = when(input.relation) {
                                MealRelationInput.BEFORE_MEAL -> MealRelation.BEFORE_MEAL
                                MealRelationInput.AFTER_MEAL -> MealRelation.AFTER_MEAL
                                MealRelationInput.WITH_MEAL -> MealRelation.WITH_MEAL
                                MealRelationInput.ANY -> MealRelation.ANY
                            }
                        )
                    },
                    imageUri = imageUri,
                    notes = notes,
                    // Mevcut state'den lowStockNotificationSent değerini al
                    lowStockNotificationSent = state.value.medicine?.lowStockNotificationSent ?: false
                )
                updateMedicineUseCase(medicine)
                _state.update { it.copy(medicine = medicine, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            try {
                deleteMedicineUseCase(medicine)
                // State güncellenmesine gerek yok çünkü ekrandan çıkılacak
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}