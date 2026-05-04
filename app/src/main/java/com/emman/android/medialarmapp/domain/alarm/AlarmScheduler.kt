package com.emman.android.medialarmapp.domain.alarm

import com.emman.android.medialarmapp.domain.models.ScheduledAlarm

interface AlarmScheduler {
    fun schedule(alarm: ScheduledAlarm)
    fun cancel(alarm: ScheduledAlarm)
    fun cancelById(requestCode: Int)
    fun rescheduleAll(alarms: List<ScheduledAlarm>)
}