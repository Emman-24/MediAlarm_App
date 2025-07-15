package com.emman.android.medialarm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.emman.android.medialarm.data.local.entities.PrivacyPolicyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrivacyPolicyDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAcceptance(acceptance: PrivacyPolicyEntity)

    @Query("SELECT * FROM privacy_policy_acceptance ORDER BY acceptanceTimestamp DESC LIMIT 1")
    fun getLatestAcceptance(): Flow<PrivacyPolicyEntity?>

}
