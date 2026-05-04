package com.emman.android.medialarmapp.di

import com.emman.android.medialarmapp.data.alarm.AndroidAlarmScheduler
import com.emman.android.medialarmapp.domain.alarm.AlarmScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(
        impl: AndroidAlarmScheduler,
    ): AlarmScheduler
}