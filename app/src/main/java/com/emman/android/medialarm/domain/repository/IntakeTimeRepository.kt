package com.emman.android.medialarm.domain.repository

import com.emman.android.medialarm.data.local.entities.IntakeTimeEntity

interface IntakeTimeRepository {
    suspend fun insertIntakeTime(intakeTime: IntakeTimeEntity): Long
    suspend fun updateIntakeTime(intakeTime: IntakeTimeEntity)
    suspend fun deleteIntakeTime(intakeTime: IntakeTimeEntity)
    suspend fun insertIntakeTimes(intakeTimes: List<IntakeTimeEntity>)
    suspend fun getIntakeTimesByScheduleId(scheduleId: Long): List<IntakeTimeEntity>
}