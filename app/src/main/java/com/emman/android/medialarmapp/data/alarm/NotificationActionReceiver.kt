package com.emman.android.medialarmapp.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.emman.android.medialarmapp.data.notification.MedicationNotificationService
import com.emman.android.medialarmapp.data.notification.NotificationChannels
import com.emman.android.medialarmapp.domain.alarm.AlarmScheduler
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import com.emman.android.medialarmapp.domain.usecases.alarm.ConfirmMedicationTakenUseCase
import com.emman.android.medialarmapp.domain.usecases.alarm.SnoozeAlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var confirmMedicationTaken: ConfirmMedicationTakenUseCase

    @Inject
    lateinit var snoozeAlarm: SnoozeAlarmUseCase

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var repository: ScheduleRepository

    @Inject
    lateinit var notificationService: MedicationNotificationService

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getStringExtra(NotificationChannels.EXTRA_ALARM_ID) ?: return logMissing("alarmId")
        val notificationId = intent.getIntExtra(NotificationChannels.EXTRA_NOTIFICATION_ID, -1)

        Log.d(TAG, "Action received: ${intent.action} for alarm [$alarmId]")

        val pendingResult = goAsync()

        scope.launch {
            try {
                when (intent.action) {
                    NotificationChannels.ACTION_TAKE_MEDICATION   -> handleTake(alarmId)
                    NotificationChannels.ACTION_SNOOZE_MEDICATION -> {
                        val minutes = intent.getIntExtra(
                            NotificationChannels.EXTRA_SNOOZE_DURATION,
                            DEFAULT_SNOOZE_MINUTES,
                        )
                        handleSnooze(alarmId, minutes)
                    }
                    else -> Log.w(TAG, "Unknown action: ${intent.action}")
                }
            }catch (e: Exception){
                Log.e(TAG, "Error handling action ${intent.action}", e)
            }finally {
                if (notificationId != -1) {
                    notificationService.dismissNotification(notificationId)
                    Log.d(TAG, "Notification [$notificationId] dismissed")
                }
                pendingResult.finish()
            }
        }


    }

    private suspend fun handleTake(alarmId: String) {
        confirmMedicationTaken(alarmId, ZonedDateTime.now())
            .onSuccess { Log.i(TAG, "Medication taken: alarm [$alarmId]") }
            .onFailure { Log.e(TAG, "Failed to confirm taken for alarm [$alarmId]", it) }
    }

    private suspend fun handleSnooze(
        alarmId: String,
        durationMinutes: Int
    ) {
        snoozeAlarm(alarmId, durationMinutes)
            .onSuccess { snoozedUntil ->
                Log.i(TAG, "Alarm [$alarmId] snoozed until $snoozedUntil")
                repository.getAlarmById(alarmId)?.let { alarm ->
                    val rescheduled = alarm.copy(scheduledTime = snoozedUntil)
                    alarmScheduler.schedule(rescheduled)
                    Log.i(TAG, "Snoozed alarm rescheduled at $snoozedUntil")
                } ?: Log.w(TAG, "Alarm [$alarmId] not found in DB — cannot reschedule snooze")
            }
            .onFailure { Log.e(TAG, "Failed to snooze alarm [$alarmId]", it) }
    }

    private fun logMissing(field: String) {
        Log.e(TAG, "Missing required extra: $field — action skipped")
    }

    companion object {
        private const val TAG = "NotifActionReceiver"
        private const val DEFAULT_SNOOZE_MINUTES = 10
    }
}