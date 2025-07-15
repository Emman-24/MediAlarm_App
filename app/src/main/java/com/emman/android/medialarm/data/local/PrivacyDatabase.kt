package com.emman.android.medialarm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emman.android.medialarm.data.local.dao.PrivacyPolicyDao
import com.emman.android.medialarm.data.local.entities.PrivacyPolicyEntity
import com.emman.android.medialarm.utils.Converters

@Database(entities = [PrivacyPolicyEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class PrivacyDatabase : RoomDatabase() {
    abstract fun privacyPolicyDao(): PrivacyPolicyDao
}