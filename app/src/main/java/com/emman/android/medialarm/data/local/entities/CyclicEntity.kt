package com.emman.android.medialarm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "cyclic",
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
data class CyclicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo("schedule_id")
    val scheduleId: Long,
    @ColumnInfo("intake_days")
    val intakeDays: Int,
    @ColumnInfo("pause_days")
    val pauseDays: Int,
    @ColumnInfo("start_time")
    val startTime: LocalDateTime,
    @ColumnInfo("created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
