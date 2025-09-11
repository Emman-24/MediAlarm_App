package com.emman.android.medialarm.data.repository

import com.emman.android.medialarm.data.local.dao.IntervalDao
import com.emman.android.medialarm.data.local.entities.IntervalEntity
import com.emman.android.medialarm.domain.repository.IntervalRepository
import javax.inject.Inject

class IntervalRepositoryImpl @Inject constructor(
    private val intervalDao: IntervalDao,
) : IntervalRepository {
    override suspend fun insert(interval: IntervalEntity) {
        intervalDao.insert(interval)
    }

    override suspend fun getByScheduleId(scheduleId: Long): IntervalEntity? {
        return intervalDao.getByScheduleId(scheduleId)
    }

}