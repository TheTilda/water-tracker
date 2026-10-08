package ru.watertracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WaterEntry::class, Settings::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "water.db").build()
    }
}
