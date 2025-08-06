package com.emman.android.medialarm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.emman.android.medialarm.data.local.dao.CyclicDao
import com.emman.android.medialarm.data.local.dao.HistoryDao
import com.emman.android.medialarm.data.local.dao.IntervalDao
import com.emman.android.medialarm.data.local.dao.MedicineDao
import com.emman.android.medialarm.data.local.dao.MedicineScheduleDao
import com.emman.android.medialarm.data.local.dao.MultipleTimesDailyDao
import com.emman.android.medialarm.data.local.dao.PrivacyPolicyDao
import com.emman.android.medialarm.data.local.dao.ScheduleDao
import com.emman.android.medialarm.data.local.dao.SpecificDaysDao
import com.emman.android.medialarm.data.local.entities.CyclicEntity
import com.emman.android.medialarm.data.local.entities.HistoryEntity
import com.emman.android.medialarm.data.local.entities.IntervalEntity
import com.emman.android.medialarm.data.local.entities.MedicineEntity
import com.emman.android.medialarm.data.local.entities.MultipleTimesDailyEntity
import com.emman.android.medialarm.data.local.entities.PrivacyPolicyEntity
import com.emman.android.medialarm.data.local.entities.ScheduleEntity
import com.emman.android.medialarm.data.local.entities.SpecificDaysEntity
import com.emman.android.medialarm.utils.Converters
import com.emman.android.medialarm.utils.DayOfWeekSetConverter
import com.emman.android.medialarm.utils.LocalDateTimeConverter
import com.emman.android.medialarm.utils.LocalTimeConverter
import com.emman.android.medialarm.utils.MedicationTimeListConverter

@Database(
    entities = [
        PrivacyPolicyEntity::class,
        MedicineEntity::class,
        ScheduleEntity::class,
        IntervalEntity::class,
        SpecificDaysEntity::class,
        CyclicEntity::class,
        MultipleTimesDailyEntity::class,
        HistoryEntity::class
    ],
    version = 5
)
@TypeConverters(
    Converters::class,
    MedicationTimeListConverter::class,
    DayOfWeekSetConverter::class,
    LocalDateTimeConverter::class,
    LocalTimeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun privacyPolicyDao(): PrivacyPolicyDao
    abstract fun medicineDao(): MedicineDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun medicineScheduleDao(): MedicineScheduleDao
    abstract fun multipleTimesDailyDao(): MultipleTimesDailyDao
    abstract fun specificDaysDao(): SpecificDaysDao
    abstract fun cyclicDao(): CyclicDao
    abstract fun intervalDao(): IntervalDao
    abstract fun historyDao(): HistoryDao
}
