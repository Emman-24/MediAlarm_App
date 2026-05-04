package com.emman.android.medialarmapp.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.emman.android.medialarmapp.data.notification.NotificationChannels
import com.emman.android.medialarmapp.domain.alarm.AlarmScheduler
import com.emman.android.medialarmapp.domain.models.ScheduledAlarm
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(alarm: ScheduledAlarm) {
        val triggerAtMillis = alarm.scheduledTime.toInstant().toEpochMilli()

        if (triggerAtMillis <= System.currentTimeMillis()) {
            Log.d(TAG, "Skipping past alarm [${alarm.id}] scheduled at ${alarm.scheduledTime}")
            return
        }

        val pendingIntent = buildAlarmPendingIntent(alarm) ?: run {
            Log.e(TAG, "Failed to build PendingIntent for alarm [${alarm.id}]")
            return
        }

        scheduleExact(triggerAtMillis, pendingIntent, alarm.id)
    }

    override fun cancel(alarm: ScheduledAlarm) = cancelById(alarm.alarmRequestCode)
    override fun cancelById(requestCode: Int) {

        val intent = Intent(context, AlarmReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
        ) ?: return

        alarmManager.cancel(pending)
        pending.cancel()
        Log.d(TAG, "Cancelled alarm with requestCode=$requestCode")
    }

    override fun rescheduleAll(alarms: List<ScheduledAlarm>) {
        Log.i(TAG, "Rescheduling ${alarms.size} alarm(s) after boot / update")
        alarms.forEach { schedule(it) }
    }

    private fun scheduleExact(
        triggerAtMillis: Long,
        pendingIntent: PendingIntent,
        alarmId: String
    ) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                    Log.d(TAG, "Exact alarm scheduled [id=$alarmId] at $triggerAtMillis")
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent,
                    )
                    Log.w(TAG, "Inexact alarm scheduled (no exact permission) [id=$alarmId]")
                }
            }

            else -> {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent,
                )
                Log.d(TAG, "Exact alarm scheduled [id=$alarmId] at $triggerAtMillis")
            }
        }
    }

    private fun buildAlarmPendingIntent(alarm: ScheduledAlarm): PendingIntent? {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(NotificationChannels.EXTRA_ALARM_ID, alarm.id)
            putExtra(NotificationChannels.EXTRA_MEDICINE_NAME, alarm.medicineName)
            putExtra(NotificationChannels.EXTRA_DOSAGE_AMOUNT, alarm.dosageAmount)
            putExtra(NotificationChannels.EXTRA_DOSAGE_UNIT, alarm.dosageUnit.name)
            putExtra(NotificationChannels.EXTRA_REQUEST_CODE, alarm.alarmRequestCode)
        }
        return PendingIntent.getBroadcast(
            context,
            alarm.alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }


    companion object {
        private const val TAG = "AndroidAlarmScheduler"
    }
}