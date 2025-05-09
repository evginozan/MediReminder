package com.evginozan.medireminder.presentation.bloodsugar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evginozan.medireminder.core.theme.Component
import com.evginozan.medireminder.domain.model.BloodSugar
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodSugarScreen(
    viewModel: BloodSugarViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Validasyon hatası için snackbar gösterimi
    LaunchedEffect(state.validationError) {
        state.validationError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearValidationError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Şeker Takibi") },
                modifier = Modifier.height(64.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Component
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Şeker Ölçümü Ekle",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.recordsByDate.isEmpty()) {
                Text(
                    text = "Henüz şeker ölçümü kaydedilmemiş",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tarihlere göre sıralı şekilde göster (en yeni tarih en üstte)
                    val sortedDates = state.recordsByDate.keys.sortedDescending()

                    items(sortedDates) { date ->
                        BloodSugarDayCard(
                            date = date,
                            records = state.recordsByDate[date] ?: emptyList(),
                            onDeleteRecord = { record ->
                                viewModel.deleteRecord(record)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBloodSugarDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { value, date, timeOfDay, isFasting, note ->
                viewModel.addRecord(
                    value = value,
                    date = date,
                    timeOfDay = timeOfDay,
                    isFasting = isFasting,
                    note = note
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BloodSugarDayCard(
    date: LocalDate,
    records: List<BloodSugar>,
    onDeleteRecord: (BloodSugar) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Tarih başlığı
            val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            Text(
                text = date.format(dateFormatter),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Her bir ölçüm için
            records.sortedWith(
                compareBy<BloodSugar> {
                    when(it.timeOfDay) {
                        com.evginozan.medireminder.domain.model.TimeOfDay.MORNING -> 0
                        com.evginozan.medireminder.domain.model.TimeOfDay.NOON -> 1
                        com.evginozan.medireminder.domain.model.TimeOfDay.AFTERNOON -> 2
                        com.evginozan.medireminder.domain.model.TimeOfDay.EVENING -> 3
                    }
                }.thenBy { it.isFasting ?: false } // önce aç, sonra tok
            ).forEach { record ->
                BloodSugarRecordItem(
                    record = record,
                    onDeleteClick = { onDeleteRecord(record) }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun BloodSugarRecordItem(
    record: BloodSugar,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Vakit ve aç/tok durumu
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sol: Vakit ve aç/tok
            Row(verticalAlignment = Alignment.CenterVertically) {
                val timeOfDayText = when(record.timeOfDay) {
                    com.evginozan.medireminder.domain.model.TimeOfDay.MORNING -> "Sabah"
                    com.evginozan.medireminder.domain.model.TimeOfDay.NOON -> "Öğle"
                    com.evginozan.medireminder.domain.model.TimeOfDay.AFTERNOON -> "İkindi"
                    com.evginozan.medireminder.domain.model.TimeOfDay.EVENING -> "Akşam"
                }

                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = timeOfDayText,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Aç/Tok durumu (eğer belirtilmişse)
                record.isFasting?.let {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (it) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = if (it) "Aç" else "Tok",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Sağ: Silme butonu
            IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Şeker değeri
        Text(
            text = "Değer: ${record.value} mg/dL",
            style = MaterialTheme.typography.bodyLarge
        )

        // Not (eğer varsa)
        record.note?.let {
            if (it.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Not: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}