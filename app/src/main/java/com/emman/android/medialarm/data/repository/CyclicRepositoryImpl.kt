package com.emman.android.medialarm.data.repository

import com.emman.android.medialarm.data.local.dao.CyclicDao
import com.emman.android.medialarm.data.local.entities.CyclicEntity
import com.emman.android.medialarm.domain.repository.CyclicRepository
import javax.inject.Inject

class CyclicRepositoryImpl @Inject constructor(
    private val cyclicDao: CyclicDao,
) : CyclicRepository {
    override suspend fun insert(cyclic: CyclicEntity) {
        cyclicDao.insert(cyclic)
    }

    override suspend fun getByScheduleId(scheduleId: Long): CyclicEntity? {
        return cyclicDao.getByScheduleId(scheduleId)
    }
}