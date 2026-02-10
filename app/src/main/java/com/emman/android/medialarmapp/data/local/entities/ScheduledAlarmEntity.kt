package com.emman.android.medialarmapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scheduled_alarms",
    foreignKeys = [
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
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
        Index(value = ["schedule_id"]),
        Index(value = ["medicine_id"]),
        Index(value = ["scheduled_time"]),
        Index(value = ["status"]),
        Index(value = ["alarm_request_code"], unique = true)
    ]
)
data class ScheduledAlarmEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "schedule_id")
    val scheduleId: Long,

    @ColumnInfo(name = "medicine_id")
    val medicineId: Long,

    @ColumnInfo(name = "medicine_name")
    val medicineName: String,

    @ColumnInfo(name = "dosage_amount")
    val dosageAmount: Double,

    @ColumnInfo(name = "dosage_unit")
    val dosageUnit: String,

    @ColumnInfo(name = "scheduled_time")
    val scheduledTime: Long, // Epoch millis UTC

    @ColumnInfo(name = "time_zone_id")
    val timeZoneId: String,

    @ColumnInfo(name = "status")
    val status: String,  // "SCHEDULED", "TAKEN", "MISSED", "SNOOZED", "CANCELLED"

    @ColumnInfo(name = "taken_at")
    val takenAt: Long? = null,  // Epoch millis UTC

    @ColumnInfo(name = "missed_at")
    val missedAt: Long? = null,

    @ColumnInfo(name = "snoozed_until")
    val snoozedUntil: Long? = null,

    @ColumnInfo(name = "alarm_request_code")
    val alarmRequestCode: Int,  //PendingIntent request code

    @ColumnInfo(name = "notification_shown")
    val notificationShown: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,
)
