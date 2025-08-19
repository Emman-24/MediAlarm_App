package com.emman.android.medialarm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicine_id")]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "medicine_id")
    val medicineId: Long,
    @ColumnInfo(name = "schedule_type")
    val scheduleType: ScheduleType,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)


enum class ScheduleType {
    MULTIPLE_TIMES_DAILY,
    SPECIFIC_DAYS,
    CYCLIC,
    INTERVAL
}

