package ru.watertracker.reminders

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.watertracker.WaterApp
import java.time.LocalDate
import java.time.LocalTime

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val repository = (applicationContext as WaterApp).repository
        val settings = repository.currentSettings()
        val total = repository.totalFor(LocalDate.now())
        val daytime = LocalTime.now().hour in 8..21 // ночью не беспокоим
        if (settings.remindersEnabled && daytime && total < settings.dailyGoalMl) {
            Notifications.showReminder(applicationContext, total, settings.dailyGoalMl)
        }
        return Result.success()
    }
}
