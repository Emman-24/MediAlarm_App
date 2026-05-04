package com.emman.android.medialarmapp.data.notification

object NotificationChannels {

    const val MEDICATION_ALARM_CHANNEL_ID = "medialarm_medication_alarm"
    const val MEDICATION_ALARM_CHANNEL_NAME = "Medication Alarms"
    const val MEDICATION_ALARM_CHANNEL_DESC =
        "High-priority reminders to take your medications on time"

    const val ACTION_TAKE_MEDICATION = "com.emman.android.medialarmapp.action.TAKE_MEDICATION"
    const val ACTION_SNOOZE_MEDICATION = "com.emman.android.medialarmapp.action.SNOOZE_MEDICATION"

    const val ACTION_OFFSET_TAKE = 1
    const val ACTION_OFFSET_SNOOZE = 2

    const val EXTRA_ALARM_ID = "extra_alarm_id"
    const val EXTRA_MEDICINE_NAME = "extra_medicine_name"
    const val EXTRA_DOSAGE_AMOUNT = "extra_dosage_amount"
    const val EXTRA_DOSAGE_UNIT = "extra_dosage_unit"
    const val EXTRA_REQUEST_CODE = "extra_request_code"
    const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
    const val EXTRA_SNOOZE_DURATION = "extra_snooze_duration"
}