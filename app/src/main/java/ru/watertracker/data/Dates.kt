package ru.watertracker.data

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDate.startMillis(): Long = atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun WaterEntry.localDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), ZoneId.systemDefault())
