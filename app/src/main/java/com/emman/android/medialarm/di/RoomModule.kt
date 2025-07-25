package com.emman.android.medialarm.di

import android.content.Context
import androidx.room.Room
import com.emman.android.medialarm.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val APP_DATABASE_NAME = "app_database"

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = APP_DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun providePrivacyPolicyDao(database: AppDatabase) = database.privacyPolicyDao()

    @Singleton
    @Provides
    fun provideMedicineDao(database: AppDatabase) = database.medicineDao()

    @Singleton
    @Provides
    fun provideScheduleDao(database: AppDatabase) = database.scheduleDao()

    @Singleton
    @Provides
    fun provideMedicineScheduleDao(database: AppDatabase) = database.medicineScheduleDao()

    @Singleton
    @Provides
    fun provideMultipleTimesDailyDao(database: AppDatabase) = database.multipleTimesDailyDao()

    @Singleton
    @Provides
    fun provideSpecificDaysDao(database: AppDatabase) = database.specificDaysDao()

    @Singleton
    @Provides
    fun provideCyclicDao(database: AppDatabase) = database.cyclicDao()

    @Singleton
    @Provides
    fun provideIntervalDao(database: AppDatabase) = database.intervalDao()

    @Singleton
    @Provides
    fun provideHistoryDao(database: AppDatabase) = database.historyDao()

}
