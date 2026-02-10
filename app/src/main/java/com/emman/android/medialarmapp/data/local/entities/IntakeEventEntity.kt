package com.emman.android.medialarmapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "intake_events",
    foreignKeys = [
        ForeignKey(
            entity = ScheduledAlarmEntity::class,
            parentColumns = ["id"],
            childColumns = ["alarm_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["alarm_id"]),
        Index(value = ["medicine_id"]),
        Index(value = ["actual_taken_time"]),
        Index(value = ["scheduled_time"])
    ]
)
data class IntakeEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "alarm_id")
    val alarmId: String,

    @ColumnInfo(name = "medicine_id")
    val medicineId: Long,

    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: Long,

    @ColumnInfo(name = "actual_taken_time")
    val actualTakenTime: Long,

    @ColumnInfo(name = "delay_minutes")
    val delayMinutes: Long,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)
