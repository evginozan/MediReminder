package com.evginozan.medireminder.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evginozan.medireminder.domain.model.Medicine
import com.evginozan.medireminder.presentation.home.AddMedicineDialog
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailScreen(
    medicineId: Long,
    viewModel: MedicineDetailViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = medicineId) {
        viewModel.loadMedicine(medicineId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("İlaç Detayı") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Düzenle"
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Sil"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.medicine != null) {
                MedicineDetailContent(
                    medicine = state.medicine!!,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            } else if (state.error != null) {
                Text(
                    text = "Hata: ${state.error}",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    if (showEditDialog && state.medicine != null) {
        AddMedicineDialog(
            initialMedicine = state.medicine,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, totalCount, dailyDoseCount, doseTimes, imageUri, notes ->
                viewModel.updateMedicine(
                    state.medicine!!.id,
                    name,
                    totalCount,
                    dailyDoseCount,
                    doseTimes,
                    imageUri,
                    notes
                )
                showEditDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("İlacı Sil") },
            text = { Text("${state.medicine?.name} ilacını silmek istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.medicine?.let { viewModel.deleteMedicine(it) }
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
fun MedicineDetailContent(
    medicine: Medicine,
    modifier: Modifier = Modifier
) {
    // İlacın detaylarını gösterecek içeriği burada oluşturun
    // Mevcut MedicineListItem'ı genişleterek başlayabilirsiniz
}