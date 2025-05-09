package com.evginozan.medireminder.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evginozan.medireminder.domain.model.DoseTime
import com.evginozan.medireminder.domain.model.MealRelation
import com.evginozan.medireminder.domain.model.Medicine
import com.evginozan.medireminder.domain.usecase.AddMedicineUseCase
import com.evginozan.medireminder.domain.usecase.DeleteMedicineUseCase
import com.evginozan.medireminder.domain.usecase.GetAllMedicinesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllMedicinesUseCase: GetAllMedicinesUseCase,
    private val addMedicineUseCase: AddMedicineUseCase,
    private val deleteMedicineUseCase: DeleteMedicineUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state = _state.asStateFlow()

    private val _effect = Channel<HomeUiEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadMedicines()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.LoadMedicines -> loadMedicines()
            is HomeUiEvent.DeleteMedicine -> deleteMedicine(event.medicine)
            is HomeUiEvent.AddMedicine -> addMedicine(
                name = event.name,
                totalCount = event.totalCount,
                dailyDoseCount = event.dailyDoseCount,
                doseTimes = event.doseTimes,
                imageUri = event.imageUri,
                notes = event.notes
            )
            is HomeUiEvent.NavigateToDetail -> navigateToDetail(event.medicineId)
        }
    }

    private fun loadMedicines() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                getAllMedicinesUseCase().collect { medicines ->
                    _state.update { it.copy(medicines = medicines, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
                _effect.send(HomeUiEffect.ShowSnackbar("Hata: ${e.message}"))
            }
        }
    }

    private fun addMedicine(
        name: String,
        totalCount: Int,
        dailyDoseCount: Int,
        doseTimes: List<DoseTimeInput>,
        imageUri: String?,
        notes: String?
    ) {
        if (name.isBlank()) {
            viewModelScope.launch {
                _effect.send(HomeUiEffect.ShowSnackbar("İlaç adı boş olamaz"))
            }
            return
        }

        if (totalCount <= 0) {
            viewModelScope.launch {
                _effect.send(HomeUiEffect.ShowSnackbar("İlaç adedi 0'dan büyük olmalı"))
            }
            return
        }

        if (dailyDoseCount <= 0) {
            viewModelScope.launch {
                _effect.send(HomeUiEffect.ShowSnackbar("Günlük doz 0'dan büyük olmalı"))
            }
            return
        }

        if (doseTimes.isEmpty()) {
            viewModelScope.launch {
                _effect.send(HomeUiEffect.ShowSnackbar("En az bir doz zamanı eklemelisiniz"))
            }
            return
        }

        val medicine = Medicine(
            name = name,
            totalCount = totalCount,
            dailyDoseCount = dailyDoseCount,
            doseTimes = doseTimes.map {
                DoseTime(
                    time = it.time,
                    relation = when(it.relation) {
                        MealRelationInput.BEFORE_MEAL -> MealRelation.BEFORE_MEAL
                        MealRelationInput.AFTER_MEAL -> MealRelation.AFTER_MEAL
                        MealRelationInput.WITH_MEAL -> MealRelation.WITH_MEAL
                        MealRelationInput.ANY -> MealRelation.ANY
                    }
                )
            },
            imageUri = imageUri,
            notes = notes
        )

        viewModelScope.launch {
            try {
                val id = addMedicineUseCase(medicine)
                _effect.send(HomeUiEffect.ShowSnackbar("İlaç başarıyla eklendi"))
            } catch (e: Exception) {
                _effect.send(HomeUiEffect.ShowSnackbar("Hata: ${e.message}"))
            }
        }
    }

    private fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch {
            try {
                deleteMedicineUseCase(medicine)
                _effect.send(HomeUiEffect.ShowSnackbar("${medicine.name} ilacı silindi"))
            } catch (e: Exception) {
                _effect.send(HomeUiEffect.ShowSnackbar("Hata: ${e.message}"))
            }
        }
    }

    private fun navigateToDetail(medicineId: Long) {
        viewModelScope.launch {
            _effect.send(HomeUiEffect.NavigateToDetail(medicineId))
        }
    }
}