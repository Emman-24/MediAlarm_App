package com.emman.android.medialarm.data.local.entities

import androidx.room.ColumnInfo     
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.emman.android.medialarm.domain.models.MedicationTime
import com.emman.android.medialarm.utils.MedicationTimeListConverter
import java.time.LocalDateTime

@Entity(
    tableName = "history",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicine_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicine_id"), Index("schedule_id")]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "medicine_id")
    val medicineId: Long,

    @ColumnInfo(name = "schedule_id")
    val scheduleId: Long,

    @ColumnInfo(name = "schedule_type")
    val scheduleType: ScheduleType,

    @ColumnInfo(name = "times")
    @TypeConverters(MedicationTimeListConverter::class)
    val times: List<MedicationTime> = emptyList(),

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)