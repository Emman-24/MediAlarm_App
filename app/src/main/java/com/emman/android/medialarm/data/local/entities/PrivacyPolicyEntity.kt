package com.emman.android.medialarm.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "privacy_policy_acceptance")
data class PrivacyPolicyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val accepted: Boolean,
    val acceptanceTimestamp: Date,
)