package ru.watertracker.reminders

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import ru.watertracker.data.Settings
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    private const val WORK_NAME = "water_reminder"

    fun apply(context: Context, settings: Settings) {
        val workManager = WorkManager.getInstance(context)
        if (!settings.remindersEnabled) {
            workManager.cancelUniqueWork(WORK_NAME)
            return
        }
        val interval = settings.reminderIntervalMin.toLong()
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(interval, TimeUnit.MINUTES)
            .setInitialDelay(interval, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, request)
    }
}
