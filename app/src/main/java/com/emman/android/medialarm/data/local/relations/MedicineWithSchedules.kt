package com.emman.android.medialarm.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.emman.android.medialarm.data.local.entities.MedicineEntity
import com.emman.android.medialarm.data.local.entities.ScheduleEntity

data class MedicineWithSchedules(
    @Embedded val medicine: MedicineEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "medicine_id"
    )
    val schedules: List<ScheduleEntity>
)