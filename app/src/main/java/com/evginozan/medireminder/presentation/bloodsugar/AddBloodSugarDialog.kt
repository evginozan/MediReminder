package com.evginozan.medireminder.presentation.bloodsugar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.evginozan.medireminder.domain.model.TimeOfDay
import java.time.LocalDate

@Composable
fun AddBloodSugarDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        value: Int,
        date: LocalDate,
        timeOfDay: TimeOfDay,
        isFasting: Boolean?,
        note: String?
    ) -> Unit
) {
    var value by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTimeOfDay by remember { mutableStateOf(TimeOfDay.MORNING) }
    var isFasting by remember { mutableStateOf<Boolean?>(null) }
    var note by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Şeker Ölçümü Ekle",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Şeker değeri
                OutlinedTextField(
                    value = value,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            value = it
                        }
                    },
                    label = { Text("Şeker Değeri (mg/dL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tarih seçimi - Bu kısım için uygun bir tarih seçici komponenti kullanılabilir
                Text(
                    text = "Tarih: ${selectedDate}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Vakit seçimi
                Text(
                    text = "Vakit",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilterChip(
                        selected = selectedTimeOfDay == TimeOfDay.MORNING,
                        onClick = { selectedTimeOfDay = TimeOfDay.MORNING },
                        label = { Text("Sabah") }
                    )

                    FilterChip(
                        selected = selectedTimeOfDay == TimeOfDay.NOON,
                        onClick = { selectedTimeOfDay = TimeOfDay.NOON },
                        label = { Text("Öğle") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilterChip(
                        selected = selectedTimeOfDay == TimeOfDay.AFTERNOON,
                        onClick = { selectedTimeOfDay = TimeOfDay.AFTERNOON },
                        label = { Text("İkindi") }
                    )

                    FilterChip(
                        selected = selectedTimeOfDay == TimeOfDay.EVENING,
                        onClick = { selectedTimeOfDay = TimeOfDay.EVENING },
                        label = { Text("Akşam") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Aç/Tok durumu (opsiyonel)
                Text(
                    text = "Aç/Tok Durumu (Opsiyonel)",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilterChip(
                        selected = isFasting == true,
                        onClick = { isFasting = true },
                        label = { Text("Aç") }
                    )

                    FilterChip(
                        selected = isFasting == false,
                        onClick = { isFasting = false },
                        label = { Text("Tok") }
                    )

                    FilterChip(
                        selected = isFasting == null,
                        onClick = { isFasting = null },
                        label = { Text("Belirtme") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Not
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Not (Opsiyonel)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Butonlar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("İptal")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onConfirm(
                                value.toIntOrNull() ?: 0,
                                selectedDate,
                                selectedTimeOfDay,
                                isFasting,
                                if (note.isBlank()) null else note
                            )
                        },
                        enabled = value.isNotBlank()
                    ) {
                        Text("Kaydet")
                    }
                }
            }
        }
    }
}