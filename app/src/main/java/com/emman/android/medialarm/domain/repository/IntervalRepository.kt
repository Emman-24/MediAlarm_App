package com.emman.android.medialarm.domain.repository

import com.emman.android.medialarm.data.local.entities.IntervalEntity

interface IntervalRepository {
    suspend fun insert(interval: IntervalEntity)
}