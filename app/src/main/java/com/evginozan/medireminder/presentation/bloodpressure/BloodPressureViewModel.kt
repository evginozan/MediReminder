package com.evginozan.medireminder.presentation.bloodpressure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evginozan.medireminder.domain.model.BloodPressure
import com.evginozan.medireminder.domain.model.TimeOfDay
import com.evginozan.medireminder.domain.usecase.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class BloodPressureViewModel(
    private val getAllBloodPressureRecordsUseCase: GetAllBloodPressureRecordsUseCase,
    private val addBloodPressureRecordUseCase: AddBloodPressureRecordUseCase,
    private val updateBloodPressureRecordUseCase: UpdateBloodPressureRecordUseCase,
    private val deleteBloodPressureRecordUseCase: DeleteBloodPressureRecordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BloodPressureUiState(isLoading = true))
    val state: StateFlow<BloodPressureUiState> = _state

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                getAllBloodPressureRecordsUseCase().collect { records ->
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
        systolic: Int,
        diastolic: Int,
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

                val record = BloodPressure(
                    systolic = systolic,
                    diastolic = diastolic,
                    date = date,
                    timeOfDay = timeOfDay,
                    isFasting = isFasting,
                    note = note
                )
                addBloodPressureRecordUseCase(record)
                _state.update { it.copy(validationError = null) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearValidationError() {
        _state.update { it.copy(validationError = null) }
    }

    fun updateRecord(
        id: Long,
        systolic: Int,
        diastolic: Int,
        date: LocalDate,
        timeOfDay: TimeOfDay,
        isFasting: Boolean?,
        note: String?
    ) {
        viewModelScope.launch {
            try {
                val record = BloodPressure(
                    id = id,
                    systolic = systolic,
                    diastolic = diastolic,
                    date = date,
                    timeOfDay = timeOfDay,
                    isFasting = isFasting,
                    note = note
                )
                updateBloodPressureRecordUseCase(record)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteRecord(record: BloodPressure) {
        viewModelScope.launch {
            try {
                deleteBloodPressureRecordUseCase(record)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}