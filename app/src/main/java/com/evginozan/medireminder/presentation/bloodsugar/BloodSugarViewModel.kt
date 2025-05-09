package com.evginozan.medireminder.presentation.bloodsugar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evginozan.medireminder.domain.model.BloodSugar
import com.evginozan.medireminder.domain.model.TimeOfDay
import com.evginozan.medireminder.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class BloodSugarViewModel(
    private val getAllBloodSugarRecordsUseCase: GetAllBloodSugarRecordsUseCase,
    private val addBloodSugarRecordUseCase: AddBloodSugarRecordUseCase,
    private val updateBloodSugarRecordUseCase: UpdateBloodSugarRecordUseCase,
    private val deleteBloodSugarRecordUseCase: DeleteBloodSugarRecordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BloodSugarUiState(isLoading = true))
    val state: StateFlow<BloodSugarUiState> = _state

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                getAllBloodSugarRecordsUseCase().collect { records ->
                    // Kayıtları tarihe göre gruplama
                    val recordsByDate = records.groupBy { it.date }
                    _state.update { it.copy(recordsByDate = recordsByDate, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun addRecord(
        value: Int,
        date: LocalDate,
        timeOfDay: TimeOfDay,
        isFasting: Boolean?,
        note: String?
    ) {
        viewModelScope.launch {
            try {
                // Aynı gün, vakit ve aç/tok durumu kontrolü
                val recordsForDate = _state.value.recordsByDate[date] ?: emptyList()
                val hasDuplicate = recordsForDate.any { existingRecord ->
                    existingRecord.timeOfDay == timeOfDay && existingRecord.isFasting == isFasting
                }

                if (hasDuplicate) {
                    _state.update {
                        it.copy(
                            validationError = "Bu gün için aynı vakit ve aç/tok durumunda kayıt zaten var."
                        )
                    }
                    return@launch
                }

                val record = BloodSugar(
                    value = value,
                    date = date,
                    timeOfDay = timeOfDay,
                    isFasting = isFasting,
                    note = note
                )
                addBloodSugarRecordUseCase(record)
                _state.update { it.copy(validationError = null) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    // Validasyon hatasını temizleme
    fun clearValidationError() {
        _state.update { it.copy(validationError = null) }
    }

    fun updateRecord(
        id: Long,
        value: Int,
        date: LocalDate,
        timeOfDay: TimeOfDay,
        isFasting: Boolean?,
        note: String?
    ) {
        viewModelScope.launch {
            try {
                val record = BloodSugar(
                    id = id,
                    value = value,
                    date = date,
                    timeOfDay = timeOfDay,
                    isFasting = isFasting,
                    note = note
                )
                updateBloodSugarRecordUseCase(record)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteRecord(record: BloodSugar) {
        viewModelScope.launch {
            try {
                deleteBloodSugarRecordUseCase(record)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}