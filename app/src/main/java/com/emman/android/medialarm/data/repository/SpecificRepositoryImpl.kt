package com.emman.android.medialarm.data.repository

import com.emman.android.medialarm.data.local.dao.SpecificDaysDao
import com.emman.android.medialarm.data.local.entities.SpecificDaysEntity
import com.emman.android.medialarm.domain.repository.SpecificRepository
import javax.inject.Inject

class SpecificRepositoryImpl @Inject constructor(
    private val specificDao: SpecificDaysDao,
) : SpecificRepository {
    override suspend fun insert(specific: SpecificDaysEntity) {
        specificDao.insert(specific)
    }

    override suspend fun getByScheduleId(scheduleId: Long): SpecificDaysEntity? {
        return specificDao.getByScheduleId(scheduleId)
    }
}