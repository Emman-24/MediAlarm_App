package com.emman.android.medialarm.common.di

import android.content.Context
import android.content.SharedPreferences
import com.emman.android.medialarm.data.repository.AppPreferences
import com.emman.android.medialarm.data.repository.PermissionRepository
import com.emman.android.medialarm.data.repository.PermissionRepositoryImpl
import com.emman.android.medialarm.data.repository.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAppPreferences(sharedPreferences: SharedPreferences): AppPreferences {
        return SharedPreferencesHelper(sharedPreferences)
    }

    @Provides
    fun providePermissionRepository(@ApplicationContext context: Context): PermissionRepository = PermissionRepositoryImpl(context)

}