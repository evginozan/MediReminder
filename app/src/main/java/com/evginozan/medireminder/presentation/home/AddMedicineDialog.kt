package com.evginozan.medireminder.presentation.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.evginozan.medireminder.domain.model.MealRelation
import com.evginozan.medireminder.domain.model.Medicine
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AddMedicineDialog(
    initialMedicine: Medicine? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        totalCount: Int,
        dailyDoseCount: Int,
        doseTimes: List<DoseTimeInput>,
        imageUri: String?,
        notes: String?
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialMedicine?.name ?: "") }
    var totalCount by remember { mutableStateOf(initialMedicine?.totalCount?.toString() ?: "") }
    var dailyDoseCount by remember { mutableStateOf(initialMedicine?.dailyDoseCount?.toString() ?: "") }
    var notes by remember { mutableStateOf(initialMedicine?.notes ?: "") }
    var imageUri by remember {
        mutableStateOf<Uri?>(initialMedicine?.imageUri?.let { Uri.parse(it) })
    }

    var doseTimes by remember {
        mutableStateOf<List<DoseTimeInput>>(
            initialMedicine?.doseTimes?.map {
                DoseTimeInput(
                    time = it.time,
                    relation = when(it.relation) {
                        MealRelation.BEFORE_MEAL -> MealRelationInput.BEFORE_MEAL
                        MealRelation.AFTER_MEAL -> MealRelationInput.AFTER_MEAL
                        MealRelation.WITH_MEAL -> MealRelationInput.WITH_MEAL
                        MealRelation.ANY -> MealRelationInput.ANY
                    }
                )
            } ?: emptyList()
        )
    }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Görsel seçme işlemi için ActivityResultLauncher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { imageUri = it }
    }

    // Günlük doz sayısını hesapla ve sınırlamalar için kullan
    val maxDoseTimes = dailyDoseCount.toIntOrNull() ?: 0
    val canAddMoreDoseTimes = doseTimes.size < maxDoseTimes

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = if (initialMedicine == null) "Yeni İlaç Ekle" else "İlacı Düzenle",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // İlaç Resmi
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                shape = CircleShape
                            )
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { imagePicker.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.AddAPhoto,
                                contentDescription = "Resim Ekle",
                                modifier = Modifier
                                    .size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // İlaç Adı
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("İlaç Adı") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Medication,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Toplam Adet
                OutlinedTextField(
                    value = totalCount,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            totalCount = it
                        }
                    },
                    label = { Text("Toplam Adet") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Numbers,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Günlük Doz Sayısı
                OutlinedTextField(
                    value = dailyDoseCount,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            dailyDoseCount = it
                            // Günlük doz değiştiğinde mevcut doz zamanlarını kontrol et
                            if (it.toIntOrNull() ?: 0 < doseTimes.size) {
                                // Yeni doz sayısı mevcut doz zamanlarından azsa, listeyi kırp
                                doseTimes = doseTimes.take(it.toIntOrNull() ?: 0)
                            }
                        }
                    },
                    label = { Text("Günlük Doz Sayısı") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Repeat,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Doz Zamanları Başlık
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Alarm,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Doz Zamanları",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    if (maxDoseTimes > 0) {
                        SuggestionChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = "${doseTimes.size}/${maxDoseTimes}",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (doseTimes.size == maxDoseTimes)
                                    MaterialTheme.colorScheme.secondaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Eklenen Doz Zamanları Listesi
                if (doseTimes.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        items(doseTimes) { doseTime ->
                            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                            val formattedTime = doseTime.time.format(timeFormatter)
                            val relationText = when (doseTime.relation) {
                                MealRelationInput.BEFORE_MEAL -> "Yemekten Önce"
                                MealRelationInput.AFTER_MEAL -> "Yemekten Sonra"
                                MealRelationInput.WITH_MEAL -> "Yemekle Birlikte"
                                MealRelationInput.ANY -> ""
                            }

                            ElevatedCard(
                                modifier = Modifier.animateContentSize(
                                    animationSpec = spring(
                                        dampingRatio = 0.7f,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                ),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        start = 16.dp,
                                        top = 8.dp,
                                        bottom = 8.dp,
                                        end = 8.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.padding(end = 8.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        Text(
                                            text = formattedTime,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        if (relationText.isNotEmpty()) {
                                            Text(
                                                text = relationText,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                            )
                                        }
                                    }

                                    FilledIconButton(
                                        onClick = {
                                            doseTimes = doseTimes.filter { it != doseTime }
                                        },
                                        modifier = Modifier.size(32.dp),
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Kaldır",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Doz Zamanı Ekleme Butonu
                Button(
                    onClick = { showTimePickerDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = canAddMoreDoseTimes && maxDoseTimes > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAlarm,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            maxDoseTimes <= 0 -> "Önce günlük doz sayısını girin"
                            !canAddMoreDoseTimes -> "Günlük doz sayısına ulaşıldı"
                            else -> "Doz Zamanı Ekle"
                        }
                    )
                }

                // Günlük doz sayısı ile eklenen doz sayısı uyumsuz ise uyarı göster
                if (maxDoseTimes > 0 && doseTimes.size < maxDoseTimes) {
                    Text(
                        text = "Lütfen tüm doz zamanlarını ekleyin (${maxDoseTimes - doseTimes.size} kaldı)",
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Notlar
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notlar (Opsiyonel)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Notes,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // İptal ve Kaydet Butonları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text("İptal")
                    }

                    Button(
                        onClick = {
                            onConfirm(
                                name,
                                totalCount.toIntOrNull() ?: 0,
                                dailyDoseCount.toIntOrNull() ?: 0,
                                doseTimes,
                                imageUri?.toString(),
                                notes.ifEmpty { null }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() &&
                                totalCount.isNotBlank() &&
                                dailyDoseCount.isNotBlank() &&
                                doseTimes.size == maxDoseTimes && // Tüm doz zamanları eklenmiş olmalı
                                maxDoseTimes > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Kaydet")
                    }
                }
            }
        }
    }

    // Doz Zamanı Ekleme Dialog Penceresi
    if (showTimePickerDialog) {
        ModernTimePickerDialog(
            onDismiss = { showTimePickerDialog = false },
            onTimeSelected = { time, relation ->
                doseTimes = doseTimes + DoseTimeInput(time, relation)
                showTimePickerDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (LocalTime, MealRelationInput) -> Unit
) {
    var hour by remember { mutableStateOf(8) }
    var minute by remember { mutableStateOf(0) }
    var selectedRelation by remember { mutableStateOf(MealRelationInput.ANY) }
    var showTimePicker by remember { mutableStateOf(false) }

    val selectedTime = LocalTime.of(hour, minute)
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onConfirm = { selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                showTimePicker = false
            },
            initialHour = hour,
            initialMinute = minute
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Doz Zamanı Ekle",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Seçilen saat gösterimi
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedTime.format(timeFormatter),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FilledTonalButton(
                            onClick = { showTimePicker = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Saati Seç")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Yemek İlişkisi Başlık
                Text(
                    text = "Yemek İlişkisi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Yemek İlişkisi Seçenekleri
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // İlk satır - İki buton yan yana
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RelationChip(
                            label = "Yemekten Önce",
                            selected = selectedRelation == MealRelationInput.BEFORE_MEAL,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            onSelected = { selectedRelation = MealRelationInput.BEFORE_MEAL },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        RelationChip(
                            label = "Yemekle",
                            selected = selectedRelation == MealRelationInput.WITH_MEAL,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            onSelected = { selectedRelation = MealRelationInput.WITH_MEAL },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // İkinci satır - İki buton yan yana
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RelationChip(
                            label = "Yemekten Sonra",
                            selected = selectedRelation == MealRelationInput.AFTER_MEAL,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            onSelected = { selectedRelation = MealRelationInput.AFTER_MEAL },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        RelationChip(
                            label = "İlişkisiz",
                            selected = selectedRelation == MealRelationInput.ANY,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            onSelected = { selectedRelation = MealRelationInput.ANY },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // İptal ve Ekle Butonları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("İptal")
                    }

                    Button(
                        onClick = {
                            onTimeSelected(LocalTime.of(hour, minute), selectedRelation)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ekle")
                    }
                }
            }
        }
    }
}

@Composable
fun RelationChip(
    label: String,
    selected: Boolean,
    color: Color,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onSelected),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) color else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected)
                    MaterialTheme.colorScheme.onSecondaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initialHour: Int,
    initialMinute: Int
) {
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Saat Seçin",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                TimePicker(state = timePickerState)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismissRequest
                    ) {
                        Text("İptal")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onConfirm(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                        }
                    ) {
                        Text("Tamam")
                    }
                }
            }
        }
    }
}