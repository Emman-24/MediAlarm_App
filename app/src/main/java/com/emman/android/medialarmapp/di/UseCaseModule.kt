package com.emman.android.medialarmapp.di

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
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideCreateMedicineUseCase(
        repository: ScheduleRepository,
    ): CreateMedicineUseCase = CreateMedicineUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideGetActiveMedicinesUseCase(
        repository: ScheduleRepository,
    ): GetActiveMedicinesUseCase = GetActiveMedicinesUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideUpdateMedicineUseCase(
        repository: ScheduleRepository,
    ): UpdateMedicineUseCase = UpdateMedicineUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideDeleteMedicineUseCase(
        repository: ScheduleRepository,
    ): DeleteMedicineUseCase = DeleteMedicineUseCase(repository)


    @Provides
    @ViewModelScoped
    fun provideScheduleMedicationUseCase(
        repository: ScheduleRepository,
        calculator: ScheduleCalculator,
    ): ScheduleMedicationUseCase = ScheduleMedicationUseCase(repository, calculator)

    @Provides
    @ViewModelScoped
    fun provideUpdateScheduleUseCase(
        repository: ScheduleRepository,
        calculator: ScheduleCalculator,
    ): UpdateScheduleUseCase = UpdateScheduleUseCase(repository, calculator)


    @Provides
    @ViewModelScoped
    fun provideGetUpcomingAlarmsUseCase(
        repository: ScheduleRepository,
    ): GetUpcomingAlarmsUseCase = GetUpcomingAlarmsUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideConfirmMedicationTakenUseCase(
        repository: ScheduleRepository,
    ): ConfirmMedicationTakenUseCase = ConfirmMedicationTakenUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideSnoozeAlarmUseCase(
        repository: ScheduleRepository,
    ): SnoozeAlarmUseCase = SnoozeAlarmUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideReconcileAlarmsUseCase(
        repository: ScheduleRepository,
    ): ReconcileAlarmsUseCase = ReconcileAlarmsUseCase(repository)
}