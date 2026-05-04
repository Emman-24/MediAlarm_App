package com.emman.android.medialarmapp

import android.app.Application
import com.emman.android.medialarmapp.data.notification.MedicationNotificationService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MediAlarmApplication : Application() {

    @Inject
    lateinit var notificationService: MedicationNotificationService

    override fun onCreate() {
        super.onCreate()
        notificationService.createNotificationChannels()
    }
}