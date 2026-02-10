package com.emman.android.medialarmapp.data.mappers

import com.emman.android.medialarmapp.data.local.entities.MedicineEntity
import com.emman.android.medialarmapp.domain.models.DosageUnit
import com.emman.android.medialarmapp.domain.models.Medicine
import com.emman.android.medialarmapp.domain.models.MedicineForm
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun Medicine.toEntity(): MedicineEntity {
    return MedicineEntity(
        id = if (id.toLongOrNull() != null) id.toLong() else 0,
        name = name,
        dosageAmount = dosageAmount,
        dosageUnit = dosageUnit.name,
        formType = form.name,
        notes = notes,
        isActive = isActive,
        createdAt = createdAt.toInstant().toEpochMilli(),
        updatedAt = updatedAt.toInstant().toEpochMilli()
    )
}


fun MedicineEntity.toDomain(zoneId: ZoneId = ZoneId.systemDefault()): Medicine {
    return Medicine(
        id = id.toString(),
        name = name,
        dosageAmount = dosageAmount,
        dosageUnit = DosageUnit.valueOf(dosageUnit),
        form = MedicineForm.valueOf(formType),
        notes = notes,
        isActive = isActive,
        createdAt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(createdAt), zoneId),
        updatedAt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), zoneId)
    )
}


fun List<MedicineEntity>.toDomain(zoneId: ZoneId = ZoneId.systemDefault()): List<Medicine> {
    return map { it.toDomain(zoneId) }
}