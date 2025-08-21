package com.emman.android.medialarm.data.repository

import com.emman.android.medialarm.data.local.dao.MultipleTimesDailyDao
import com.emman.android.medialarm.data.local.entities.MultipleTimesDailyEntity
import com.emman.android.medialarm.domain.repository.MultipleTimesRepository
import javax.inject.Inject

class MultipleTimesRepositoryImpl @Inject constructor(
    private val multipleTimesDailyDao: MultipleTimesDailyDao,
) : MultipleTimesRepository {
    override suspend fun insert(multipleTimes: MultipleTimesDailyEntity) {
        multipleTimesDailyDao.insert(multipleTimes)
    }
}