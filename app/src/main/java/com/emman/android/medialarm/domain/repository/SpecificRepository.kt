package com.emman.android.medialarm.domain.repository

import com.emman.android.medialarm.data.local.entities.SpecificDaysEntity

interface SpecificRepository {
    suspend fun insert(specific: SpecificDaysEntity)
}