package ru.watertracker.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HistoryTest {
    private val today = LocalDate.of(2026, 10, 8)

    private fun entry(date: LocalDate, ml: Int) =
        WaterEntry(amountMl = ml, dateTime = date.startMillis() + 12 * 3_600_000L)

    @Test
    fun emptyHistory() = assertEquals(emptyList<DayTotal>(), buildHistory(emptyList(), today))

    @Test
    fun daysWithoutEntriesAreZero() {
        val history = buildHistory(listOf(entry(today.minusDays(3), 250), entry(today, 500), entry(today, 250)), today)
        assertEquals(listOf(750, 0, 0, 250), history.map { it.totalMl })
        assertEquals(today, history.first().date)
    }
}
