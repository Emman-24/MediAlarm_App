package com.emman.android.medialarm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "specific_days",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("schedule_id")]
)
data class SpecificDaysEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("schedule_id")
    val scheduleId: Long,
    val onSunday: Boolean = false,
    val onMonday: Boolean = false,
    val onTuesday: Boolean = false,
    val onWednesday: Boolean = false,
    val onThursday: Boolean = false,
    val onFriday: Boolean = false,
    val onSaturday: Boolean = false,
    @ColumnInfo("created_at")
    val createdAt: Long = System.currentTimeMillis(),

    )
