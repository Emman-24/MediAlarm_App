package com.emman.android.medialarm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.emman.android.medialarm.utils.DayOfWeekSetConverter
import java.time.DayOfWeek

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
    val id: Long,
    @ColumnInfo("schedule_id")
    val scheduleId: Long,
    @ColumnInfo("days_of_week")
    @TypeConverters(DayOfWeekSetConverter::class)
    val daysOfWeek: Set<DayOfWeek>,
)
