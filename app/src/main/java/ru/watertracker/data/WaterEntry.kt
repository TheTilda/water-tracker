package ru.watertracker.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Одна выпитая порция воды. [dateTime] — момент добавления, epoch millis. */
@Entity(tableName = "water_entries", indices = [Index("dateTime")])
data class WaterEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val dateTime: Long,
)
