package com.emman.android.medialarm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.emman.android.medialarm.domain.models.MedicationTime
import com.emman.android.medialarm.utils.MedicationTimeListConverter

@Entity(
    tableName = "multiple_times_daily",
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
data class MultipleTimesDailyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo("schedule_id")
    val scheduleId: Long,
    @ColumnInfo("intake_times")
    val intakeTimes: Int,
    @ColumnInfo(name = "times")
    @TypeConverters(MedicationTimeListConverter::class)
    val times: List<MedicationTime> = emptyList()
)
