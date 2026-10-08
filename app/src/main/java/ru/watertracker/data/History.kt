package ru.watertracker.data

import java.time.LocalDate

data class DayTotal(val date: LocalDate, val totalMl: Int)

/** Дни от сегодня назад до первой записи; дни без записей идут с нулём. */
fun buildHistory(entries: List<WaterEntry>, today: LocalDate): List<DayTotal> {
    if (entries.isEmpty()) return emptyList()
    val totals = entries.groupBy { it.localDateTime().toLocalDate() }.mapValues { it.value.sumOf(WaterEntry::amountMl) }
    val first = minOf(totals.keys.min(), today)
    return generateSequence(today) { it.minusDays(1) }
        .takeWhile { !it.isBefore(first) }
        .map { DayTotal(it, totals[it] ?: 0) }
        .toList()
}
