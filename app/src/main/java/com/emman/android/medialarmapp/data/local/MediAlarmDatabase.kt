package com.emman.android.medialarmapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emman.android.medialarmapp.data.local.converters.Converters
import com.emman.android.medialarmapp.data.local.dao.AlarmDao
import com.emman.android.medialarmapp.data.local.dao.IntakeEventDao
import com.emman.android.medialarmapp.data.local.dao.MedicineDao
import com.emman.android.medialarmapp.data.local.dao.ScheduleDao
import com.emman.android.medialarmapp.data.local.entities.IntakeEventEntity
import com.emman.android.medialarmapp.data.local.entities.MedicineEntity
import com.emman.android.medialarmapp.data.local.entities.ScheduleEntity
import com.emman.android.medialarmapp.data.local.entities.ScheduledAlarmEntity

@Database(
    entities = [
        MedicineEntity::class,
        ScheduleEntity::class,
        ScheduledAlarmEntity::class,
        IntakeEventEntity::class
    ],
    version = 1,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class MediAlarmDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun alarmDao(): AlarmDao
    abstract fun intakeEventDao(): IntakeEventDao

    companion object {
        const val DATABASE_NAME = "medialarm.db"

        fun startOfDay(timeMillis: Long): Long {
            val instant = java.time.Instant.ofEpochMilli(timeMillis)
            val localDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            return localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        fun endOfDay(timeMillis: Long): Long {
            val instant = java.time.Instant.ofEpochMilli(timeMillis)
            val localDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            return localDate.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }
}