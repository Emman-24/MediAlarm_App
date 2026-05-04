package com.emman.android.medialarmapp.data.notification

import android.Manifest
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.emman.android.medialarmapp.MainActivity
import com.emman.android.medialarmapp.data.alarm.NotificationActionReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicationNotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    fun createNotificationChannels() {
        val alarmChannel = NotificationChannel(
            NotificationChannels.MEDICATION_ALARM_CHANNEL_ID,
            NotificationChannels.MEDICATION_ALARM_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = NotificationChannels.MEDICATION_ALARM_CHANNEL_DESC
            enableLights(true)
            enableVibration(true)
            vibrationPattern = VIBRATION_PATTERN
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setBypassDnd(true)
        }
        notificationManager.createNotificationChannel(alarmChannel)
        Log.d(TAG, "Notification channels registered")
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showMedicationAlarmNotification(
        alarmId: String,
        medicineName: String,
        dosageAmount: Double,
        dosageUnit: String,
        notificationId: Int,
    ) {
        if (!hasPostNotificationsPermission()) {
            Log.w(TAG, "POST_NOTIFICATIONS permission not granted — skipping notification")
            return
        }
        val dosageText = formatDosage(dosageAmount, dosageUnit)
        val contentTitle = "💊 Time to take your medication"
        val contentText = "$medicineName  ·  $dosageText"

        val notification = NotificationCompat
            .Builder(context, NotificationChannels.MEDICATION_ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_dialog_info)

            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOngoing(false)
            .setDefaults(Notification.DEFAULT_ALL)
            .setFullScreenIntent(buildFullScreenIntent(), true)
            .setContentIntent(buildContentIntent())
            .addAction(buildTakeAction(alarmId, notificationId))
            .addAction(buildSnoozeAction(alarmId, notificationId))
            .build()

        notificationManager.notify(notificationId, notification)
        Log.d(TAG, "Notification shown [alarmId=$alarmId, notifId=$notificationId]")
    }

    fun dismissNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    fun dismissAll() {
        notificationManager.cancelAll()
    }

    private fun buildContentIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            REQUEST_CONTENT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun buildFullScreenIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_SHOW_ALARM, true)
        }
        return PendingIntent.getActivity(
            context,
            REQUEST_FULLSCREEN,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun buildTakeAction(alarmId: String, notificationId: Int): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationChannels.ACTION_TAKE_MEDICATION
            putExtra(NotificationChannels.EXTRA_ALARM_ID,        alarmId)
            putExtra(NotificationChannels.EXTRA_NOTIFICATION_ID, notificationId)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            notificationId * REQUEST_MULTIPLIER + NotificationChannels.ACTION_OFFSET_TAKE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Action(
            android.R.drawable.ic_input_add,
            "✓ Taken",
            pending,
        )
    }

    private fun buildSnoozeAction(alarmId: String, notificationId: Int): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = NotificationChannels.ACTION_SNOOZE_MEDICATION
            putExtra(NotificationChannels.EXTRA_ALARM_ID,        alarmId)
            putExtra(NotificationChannels.EXTRA_NOTIFICATION_ID, notificationId)
            putExtra(NotificationChannels.EXTRA_SNOOZE_DURATION, DEFAULT_SNOOZE_MINUTES)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            notificationId * REQUEST_MULTIPLIER + NotificationChannels.ACTION_OFFSET_SNOOZE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Action(
            android.R.drawable.ic_lock_idle_alarm,
            "⏰ Snooze ${DEFAULT_SNOOZE_MINUTES} min",
            pending,
        )
    }


    private fun hasPostNotificationsPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ActivityCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun formatDosage(amount: Double, unitName: String): String {
        val abbreviated = UNIT_ABBREVIATIONS[unitName] ?: unitName.lowercase()
        val formatted = if (amount == amount.toLong().toDouble()) {
            amount.toLong().toString()
        } else {
            amount.toString()
        }
        return "$formatted $abbreviated"
    }


    companion object {
        private const val TAG = "MedNotificationService"
        private const val DEFAULT_SNOOZE_MINUTES = 10
        private const val REQUEST_CONTENT = 900_000
        private const val REQUEST_FULLSCREEN = 900_001
        private const val REQUEST_MULTIPLIER = 10
        const val EXTRA_SHOW_ALARM = "extra_show_alarm"

        private val VIBRATION_PATTERN = longArrayOf(0L, 500L, 200L, 500L, 200L, 500L)

        private val UNIT_ABBREVIATIONS = mapOf(
            "MILLIGRAMS" to "mg",
            "GRAMS" to "g",
            "MILLILITERS" to "ml",
            "UNITS" to "U",
            "MICROGRAMS" to "mcg",
            "INTERNATIONAL_UNITS" to "IU",
            "PERCENTAGE" to "%",
            "DROPS" to "drops",
        )
    }
}