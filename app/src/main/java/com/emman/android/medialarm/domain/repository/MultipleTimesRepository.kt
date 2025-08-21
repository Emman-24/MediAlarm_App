package com.emman.android.medialarm.domain.repository

import com.emman.android.medialarm.data.local.entities.MultipleTimesDailyEntity

interface MultipleTimesRepository {
    suspend fun insert(multipleTimes: MultipleTimesDailyEntity)
}