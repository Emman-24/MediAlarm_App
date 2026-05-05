package com.emman.android.medialarmapp.di

import com.emman.android.medialarmapp.domain.alarm.AlarmScheduler
import com.emman.android.medialarmapp.domain.calculator.ScheduleCalculator
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import com.emman.android.medialarmapp.domain.usecases.alarm.ConfirmMedicationTakenUseCase
import com.emman.android.medialarmapp.domain.usecases.alarm.GetUpcomingAlarmsUseCase
import com.emman.android.medialarmapp.domain.usecases.alarm.ReconcileAlarmsUseCase
import com.emman.android.medialarmapp.domain.usecases.alarm.SnoozeAlarmUseCase
import com.emman.android.medialarmapp.domain.usecases.medicine.CreateMedicineUseCase
import com.emman.android.medialarmapp.domain.usecases.medicine.DeleteMedicineUseCase
import com.emman.android.medialarmapp.domain.usecases.medicine.GetActiveMedicinesUseCase
import com.emman.android.medialarmapp.domain.usecases.medicine.UpdateMedicineUseCase
import com.emman.android.medialarmapp.domain.usecases.schedule.ScheduleMedicationUseCase
import com.emman.android.medialarmapp.domain.usecases.schedule.UpdateScheduleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {


    // ── Medicine use cases ────────────────────────────────────────────────────
    @Provides
    fun provideCreateMedicineUseCase(
        repository: ScheduleRepository,
    ): CreateMedicineUseCase = CreateMedicineUseCase(repository)

    @Provides
    fun provideGetActiveMedicinesUseCase(
        repository: ScheduleRepository,
    ): GetActiveMedicinesUseCase = GetActiveMedicinesUseCase(repository)

    @Provides
    fun provideUpdateMedicineUseCase(
        repository: ScheduleRepository,
    ): UpdateMedicineUseCase = UpdateMedicineUseCase(repository)

    @Provides
    fun provideDeleteMedicineUseCase(
        repository: ScheduleRepository,
    ): DeleteMedicineUseCase = DeleteMedicineUseCase(repository)


    // ── Schedule use cases ────────────────────────────────────────────────────
    @Provides
    fun provideScheduleMedicationUseCase(
        repository: ScheduleRepository,
        calculator: ScheduleCalculator,
        alarmScheduler: AlarmScheduler,
    ): ScheduleMedicationUseCase = ScheduleMedicationUseCase(repository, calculator, alarmScheduler)

    @Provides
    fun provideUpdateScheduleUseCase(
        repository: ScheduleRepository,
        calculator: ScheduleCalculator,
        alarmScheduler: AlarmScheduler,
    ): UpdateScheduleUseCase = UpdateScheduleUseCase(repository, calculator, alarmScheduler)


    // ── Alarm use cases ───────────────────────────────────────────────────────
    @Provides
    fun provideGetUpcomingAlarmsUseCase(
        repository: ScheduleRepository,
    ): GetUpcomingAlarmsUseCase = GetUpcomingAlarmsUseCase(repository)

    @Provides
    fun provideConfirmMedicationTakenUseCase(
        repository: ScheduleRepository,
    ): ConfirmMedicationTakenUseCase = ConfirmMedicationTakenUseCase(repository)

    @Provides
    fun provideSnoozeAlarmUseCase(
        repository: ScheduleRepository,
        alarmScheduler: AlarmScheduler,
    ): SnoozeAlarmUseCase = SnoozeAlarmUseCase(repository, alarmScheduler)

    @Provides
    fun provideReconcileAlarmsUseCase(
        repository: ScheduleRepository,
    ): ReconcileAlarmsUseCase = ReconcileAlarmsUseCase(repository)
}
