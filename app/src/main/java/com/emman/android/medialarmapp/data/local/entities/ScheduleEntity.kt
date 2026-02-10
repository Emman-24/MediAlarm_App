package com.emman.android.medialarmapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedules",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["medicine_id"]),
        Index(value = ["is_active"]),
        Index(value = ["schedule_type"])
    ]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "medicine_id")
    val medicineId: Long,

    @ColumnInfo(name = "schedule_type")
    val scheduleType: String,  // "INTERVAL", "TIMES_PER_DAY", etc.

    @ColumnInfo(name = "pattern_json")
    val patternJson: String,

    @ColumnInfo(name = "start_date")
    val startDate: Long,

    @ColumnInfo(name = "end_date")
    val endDate: Long? = null,

    @ColumnInfo(name = "time_zone_id")
    val timeZoneId: String,  // "America/Bogota", "America/New_York"

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)