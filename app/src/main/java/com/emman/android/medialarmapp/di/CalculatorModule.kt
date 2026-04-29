package com.emman.android.medialarmapp.di

import com.emman.android.medialarmapp.domain.calculator.DefaultScheduleCalculator
import com.emman.android.medialarmapp.domain.calculator.ScheduleCalculator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CalculatorModule {
    @Binds
    @Singleton
    abstract fun bindScheduleCalculator(
        impl: DefaultScheduleCalculator,
    ): ScheduleCalculator
}