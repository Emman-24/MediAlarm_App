package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emman.android.medialarm.data.local.entities.IntakeTimeEntity

@Dao
interface IntakeTimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(intakeTime: IntakeTimeEntity): Long

    @Update
    suspend fun update(intakeTime: IntakeTimeEntity)

    @Delete
    suspend fun delete(intakeTime: IntakeTimeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntakeTimes(intakeTimes: List<IntakeTimeEntity>)

    @Query("SELECT * FROM intake_times WHERE schedule_id = :scheduleId")
    suspend fun getIntakeTimesByScheduleId(scheduleId: Long): List<IntakeTimeEntity>


}