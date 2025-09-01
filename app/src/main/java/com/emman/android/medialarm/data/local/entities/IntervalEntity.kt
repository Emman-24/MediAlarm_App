package com.emman.android.medialarm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.LocalTime


@Entity(
    tableName = "interval",
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
data class IntervalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "schedule_id")
    val scheduleId: Long,

    @ColumnInfo(name = "interval_unit")
    val intervalUnit: IntervalUnit,

    @ColumnInfo(name = "interval_value")
    val intervalValue: Int,

    @ColumnInfo(name = "start_time")
    val startTime: LocalTime? = null,

    @ColumnInfo(name = "end_time")
    val endTime: LocalTime? = null,

    @ColumnInfo(name = "start_date")
    val startDate: LocalDateTime,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
) {
    init {
        require(intervalValue > 0) { "Interval value must be positive" }
    }
}

enum class IntervalUnit(val displayName: String) {
    DAYS("Days"),
    HOURS("Hours")
}
