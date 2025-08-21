package com.emman.android.medialarm.data.repository

import com.emman.android.medialarm.data.local.dao.IntakeTimeDao
import com.emman.android.medialarm.data.local.entities.IntakeTimeEntity
import com.emman.android.medialarm.domain.repository.IntakeTimeRepository
import javax.inject.Inject

class IntakeTimeRepositoryImpl @Inject constructor(
    private val intakeTimeDao: IntakeTimeDao,
) : IntakeTimeRepository {
    override suspend fun insertIntakeTime(intakeTime: IntakeTimeEntity): Long {
        return intakeTimeDao.insert(intakeTime)
    }

    override suspend fun updateIntakeTime(intakeTime: IntakeTimeEntity) {
        intakeTimeDao.update(intakeTime)
    }

    override suspend fun deleteIntakeTime(intakeTime: IntakeTimeEntity) {
        intakeTimeDao.delete(intakeTime)
    }

    override suspend fun insertIntakeTimes(intakeTimes: List<IntakeTimeEntity>) {
        intakeTimeDao.insertIntakeTimes(intakeTimes)
    }
}