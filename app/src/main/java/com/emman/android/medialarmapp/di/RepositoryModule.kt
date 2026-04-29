package com.emman.android.medialarmapp.di

import com.emman.android.medialarmapp.data.repositories.ScheduleRepositoryImpl
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(impl: ScheduleRepositoryImpl, ): ScheduleRepository

}