package com.evginozan.medireminder.data.mapper

import com.evginozan.medireminder.data.local.entity.MedicineEntity
import com.evginozan.medireminder.data.local.entity.DoseTime as EntityDoseTime
import com.evginozan.medireminder.data.local.entity.MealRelation as EntityMealRelation
import com.evginozan.medireminder.domain.model.Medicine
import com.evginozan.medireminder.domain.model.DoseTime as DomainDoseTime
import com.evginozan.medireminder.domain.model.MealRelation as DomainMealRelation
import java.time.LocalTime

fun MedicineEntity.toDomain(): Medicine {
    return Medicine(
        id = id,
        name = name,
        totalCount = totalCount,
        dailyDoseCount = dailyDoseCount,
        doseTimes = doseTimes.map {
            DomainDoseTime(
                time = LocalTime.of(it.hour, it.minute),
                relation = when(it.relation) {
                    EntityMealRelation.BEFORE_MEAL -> DomainMealRelation.BEFORE_MEAL
                    EntityMealRelation.AFTER_MEAL -> DomainMealRelation.AFTER_MEAL
                    EntityMealRelation.WITH_MEAL -> DomainMealRelation.WITH_MEAL
                    EntityMealRelation.ANY -> DomainMealRelation.ANY
                }
            )
        },
        imageUri = imageUri,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Medicine.toEntity(): MedicineEntity {
    return MedicineEntity(
        id = id,
        name = name,
        totalCount = totalCount,
        dailyDoseCount = dailyDoseCount,
        doseTimes = doseTimes.map {
            EntityDoseTime(
                hour = it.time.hour,
                minute = it.time.minute,
                relation = when(it.relation) {
                    DomainMealRelation.BEFORE_MEAL -> EntityMealRelation.BEFORE_MEAL
                    DomainMealRelation.AFTER_MEAL -> EntityMealRelation.AFTER_MEAL
                    DomainMealRelation.WITH_MEAL -> EntityMealRelation.WITH_MEAL
                    DomainMealRelation.ANY -> EntityMealRelation.ANY
                }
            )
        },
        imageUri = imageUri,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}