package com.emman.android.medialarm.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emman.android.medialarm.domain.models.DosageUnit
import com.emman.android.medialarm.domain.models.MedicineForm
import java.time.LocalDateTime

@Entity(
    tableName = "medicines"
)
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val dosageAmount: Double,
    val dosageUnit: DosageUnit,
    val formType: MedicineForm,
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)


