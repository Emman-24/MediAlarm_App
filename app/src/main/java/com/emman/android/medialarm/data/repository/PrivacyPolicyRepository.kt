package com.emman.android.medialarm.data.repository

import com.emman.android.medialarm.data.local.dao.PrivacyPolicyDao
import com.emman.android.medialarm.data.local.entities.PrivacyPolicyEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date
import javax.inject.Inject

class PrivacyPolicyRepository @Inject constructor(
    private val privacyPolicyDao: PrivacyPolicyDao,
) {

    val latestAcceptance: Flow<PrivacyPolicyEntity?> = privacyPolicyDao.getLatestAcceptance()

    suspend fun hasUserAcceptedPolicy(): Boolean {
        return latestAcceptance.firstOrNull()?.accepted == true
    }

    suspend fun saveAcceptance(accepted: Boolean) {
        val acceptance = PrivacyPolicyEntity(
            accepted = accepted,
            acceptanceTimestamp = Date()
        )
        privacyPolicyDao.insertAcceptance(acceptance)
    }


}