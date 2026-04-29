package com.emman.android.medialarmapp.di

import android.content.Context
import androidx.room.Room
import com.emman.android.medialarmapp.data.local.MediAlarmDatabase
import com.emman.android.medialarmapp.data.local.dao.AlarmDao
import com.emman.android.medialarmapp.data.local.dao.IntakeEventDao
import com.emman.android.medialarmapp.data.local.dao.MedicineDao
import com.emman.android.medialarmapp.data.local.dao.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMediAlarmDatabase(
        @ApplicationContext context: Context,
    ): MediAlarmDatabase = Room.databaseBuilder(
        context,
        MediAlarmDatabase::class.java,
        MediAlarmDatabase.DATABASE_NAME
    )
        .fallbackToDestructiveMigration(dropAllTables = false)
        .build()

    @Provides
    @Singleton
    fun provideMedicineDao(database: MediAlarmDatabase): MedicineDao =
        database.medicineDao()

    @Provides
    @Singleton
    fun provideScheduleDao(database: MediAlarmDatabase): ScheduleDao =
        database.scheduleDao()

    @Provides
    @Singleton
    fun provideAlarmDao(database: MediAlarmDatabase): AlarmDao =
        database.alarmDao()

    @Provides
    @Singleton
    fun provideIntakeEventDao(database: MediAlarmDatabase): IntakeEventDao =
        database.intakeEventDao()
}