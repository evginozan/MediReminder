package com.evginozan.medireminder.data.mapper

import com.evginozan.medireminder.data.local.entity.BloodPressureEntity
import com.evginozan.medireminder.domain.model.BloodPressure
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun BloodPressureEntity.toBloodPressure(): BloodPressure {
    return BloodPressure(
        id = id,
        systolic = systolic,
        diastolic = diastolic,
        date = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate(),
        timeOfDay = when(timeOfDay) {
            com.evginozan.medireminder.data.local.entity.TimeOfDay.MORNING -> com.evginozan.medireminder.domain.model.TimeOfDay.MORNING
            com.evginozan.medireminder.data.local.entity.TimeOfDay.NOON -> com.evginozan.medireminder.domain.model.TimeOfDay.NOON
            com.evginozan.medireminder.data.local.entity.TimeOfDay.AFTERNOON -> com.evginozan.medireminder.domain.model.TimeOfDay.AFTERNOON
            com.evginozan.medireminder.data.local.entity.TimeOfDay.EVENING -> com.evginozan.medireminder.domain.model.TimeOfDay.EVENING
        },
        isFasting = isFasting,
        note = note
    )
}

fun BloodPressure.toEntity(): BloodPressureEntity {
    return BloodPressureEntity(
        id = id,
        systolic = systolic,
        diastolic = diastolic,
        date = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        timeOfDay = when(timeOfDay) {
            com.evginozan.medireminder.domain.model.TimeOfDay.MORNING -> com.evginozan.medireminder.data.local.entity.TimeOfDay.MORNING
            com.evginozan.medireminder.domain.model.TimeOfDay.NOON -> com.evginozan.medireminder.data.local.entity.TimeOfDay.NOON
            com.evginozan.medireminder.domain.model.TimeOfDay.AFTERNOON -> com.evginozan.medireminder.data.local.entity.TimeOfDay.AFTERNOON
            com.evginozan.medireminder.domain.model.TimeOfDay.EVENING -> com.evginozan.medireminder.data.local.entity.TimeOfDay.EVENING
        },
        isFasting = isFasting,
        note = note
    )
}