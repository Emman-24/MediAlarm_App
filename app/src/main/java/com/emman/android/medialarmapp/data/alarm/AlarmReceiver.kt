package com.emman.android.medialarmapp.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.emman.android.medialarmapp.data.notification.MedicationNotificationService
import com.emman.android.medialarmapp.data.notification.NotificationChannels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: MedicationNotificationService

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive — action=${intent.action}")

        val alarmId = intent.getStringExtra(NotificationChannels.EXTRA_ALARM_ID)
            ?: return logMissing("alarmId")
        val medicineName = intent.getStringExtra(NotificationChannels.EXTRA_MEDICINE_NAME)
            ?: return logMissing("medicineName")
        val dosageAmount = intent.getDoubleExtra(NotificationChannels.EXTRA_DOSAGE_AMOUNT, 0.0)
        val dosageUnit = intent.getStringExtra(NotificationChannels.EXTRA_DOSAGE_UNIT)
            ?: return logMissing("dosageUnit")
        val requestCode = intent.getIntExtra(NotificationChannels.EXTRA_REQUEST_CODE, 0)


        notificationService.showMedicationAlarmNotification(
            alarmId       = alarmId,
            medicineName  = medicineName,
            dosageAmount  = dosageAmount,
            dosageUnit    = dosageUnit,
            notificationId = requestCode,
        )

        Log.i(TAG, "Notification shown for alarm [$alarmId] — $medicineName")

    }

    private fun logMissing(key: String) {
        Log.e(TAG, "Missing required intent extra: $key")
    }

    companion object {
        private const val TAG = "AlarmReceiver"
    }

}