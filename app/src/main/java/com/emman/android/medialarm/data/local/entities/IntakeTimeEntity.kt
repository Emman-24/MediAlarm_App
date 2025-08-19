package com.emman.android.medialarm.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(
    tableName = "intake_times",
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
data class IntakeTimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo("schedule_id")
    val scheduleId: Long,
    @ColumnInfo("intake_time")
    val intakeTime: LocalTime,
    @ColumnInfo("quantity")
    val quantity: Double,
    @ColumnInfo(name = "intake_advice")
    val intakeAdvice: IntakeAdvice,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)

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