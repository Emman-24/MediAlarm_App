package com.emman.android.medialarmapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "medicines",
    indices = [
        Index(value = ["is_active"]),
        Index(value = ["name"])
    ]
)
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "dosage_amount")
    val dosageAmount: Double,

    @ColumnInfo(name = "dosage_unit")
    val dosageUnit: String,

    @ColumnInfo(name = "form_type")
    val formType: String,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long,
)
