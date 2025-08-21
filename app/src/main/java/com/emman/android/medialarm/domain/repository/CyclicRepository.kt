package com.emman.android.medialarm.domain.repository

import com.emman.android.medialarm.data.local.entities.CyclicEntity

interface CyclicRepository {
    suspend fun insert(cyclic: CyclicEntity)
}