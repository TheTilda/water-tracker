package ru.watertracker

import android.app.Application
import ru.watertracker.data.AppDatabase
import ru.watertracker.data.WaterRepository
import ru.watertracker.reminders.Notifications

class WaterApp : Application() {
    val repository: WaterRepository by lazy { WaterRepository(AppDatabase.build(this)) }

    override fun onCreate() {
        super.onCreate()
        Notifications.createChannel(this)
    }
}
