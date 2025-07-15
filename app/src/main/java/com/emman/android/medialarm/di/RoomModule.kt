package com.emman.android.medialarm.di

import android.content.Context
import androidx.room.Room
import com.emman.android.medialarm.data.local.PrivacyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val PRIVACY_DATABASE_NAME = "privacy_database"

    @Singleton
    @Provides
    fun providePrivacyDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context = context,
            klass = PrivacyDatabase::class.java,
            name = PRIVACY_DATABASE_NAME
        ).build()


    @Singleton
    @Provides
    fun providePrivacyPolicyDao(database: PrivacyDatabase) = database.privacyPolicyDao()


}