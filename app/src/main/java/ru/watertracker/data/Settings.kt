package ru.watertracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Настройки пользователя: в таблице одна строка с id = 1. */
@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = 1,
    val dailyGoalMl: Int = 2000,
    val portionMl: Int = 250,
    val remindersEnabled: Boolean = false,
    val reminderIntervalMin: Int = 60,
)
