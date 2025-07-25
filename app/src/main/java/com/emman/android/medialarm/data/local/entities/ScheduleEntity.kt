package com.emman.android.medialarm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicine_id")]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "medicine_id")
    val medicineId: Long,
    @ColumnInfo(name = "schedule_type")
    val scheduleType: ScheduleType,
    @ColumnInfo(name = "intake_advice")
    val intakeAdvice: IntakeAdvice,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)


enum class ScheduleType {
    MULTIPLE_TIMES_DAILY,
    SPECIFIC_DAYS,
    CYCLIC,
    INTERVAL
}

enum class IntakeAdvice(val displayName: String, val description: String) {
    NONE("No specific advice", "Take as prescribed"),
    BEFORE_MEAL("Before meal", "Take 30-60 minutes before eating"),
    WITH_MEAL("With meal", "Take during or immediately after eating"),
    AFTER_MEAL("After meal", "Take 1-2 hours after eating"),
    ON_EMPTY_STOMACH("On empty stomach", "Take when stomach is empty"),
    WITH_WATER("With water", "Take with plenty of water"),
    AVOID_ALCOHOL("Avoid alcohol", "Do not consume alcohol while taking"),
    AVOID_DAIRY("Avoid dairy", "Do not take with dairy products"),
    CUSTOM("Custom", "See notes for specific instructions")
}