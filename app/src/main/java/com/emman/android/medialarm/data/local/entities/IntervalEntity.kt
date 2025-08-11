package com.emman.android.medialarm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.emman.android.medialarm.domain.models.MedicationTime
import com.emman.android.medialarm.utils.MedicationTimeListConverter

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
    @ColumnInfo(name = "times")
    @TypeConverters(MedicationTimeListConverter::class)
    val times: List<MedicationTime> = emptyList()
)


enum class IntervalUnit(val displayName: String) {
    DAYS("Days"),
    HOURS("Hours")
}


