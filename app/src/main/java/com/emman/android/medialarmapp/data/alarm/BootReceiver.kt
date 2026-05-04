package com.emman.android.medialarmapp.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.emman.android.medialarmapp.domain.alarm.AlarmScheduler
import com.emman.android.medialarmapp.domain.repositories.ScheduleRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: ScheduleRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())


    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in BOOT_ACTIONS) {
            Log.d(TAG, "Ignoring unrelated action: ${intent.action}")
            return
        }

        Log.i(TAG, "Boot event received [${intent.action}] — rescheduling alarms")
        val pendingResult = goAsync()

        scope.launch {
            try {
                val alarms = repository.getAllScheduledAlarms()
                Log.i(TAG, "Rescheduling ${alarms.size} alarm(s)")
                alarmScheduler.rescheduleAll(alarms)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reschedule alarms after boot", e)
            } finally {
                pendingResult.finish()
            }
        }

    }

    companion object {
        private const val TAG = "BootReceiver"

        private val BOOT_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
        )
    }

}