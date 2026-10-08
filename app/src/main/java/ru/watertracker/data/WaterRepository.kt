package ru.watertracker.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class WaterRepository(db: AppDatabase) {
    private val waterDao = db.waterDao()
    private val settingsDao = db.settingsDao()

    val settings: Flow<Settings> = settingsDao.observe().map { it ?: Settings() }
    val allEntries: Flow<List<WaterEntry>> = waterDao.observeAll()

    suspend fun currentSettings(): Settings = settingsDao.get() ?: Settings()
    suspend fun saveSettings(settings: Settings) = settingsDao.save(settings)

    fun entriesFor(date: LocalDate): Flow<List<WaterEntry>> =
        waterDao.observeBetween(date.startMillis(), date.plusDays(1).startMillis())

    suspend fun totalFor(date: LocalDate): Int =
        waterDao.sumBetween(date.startMillis(), date.plusDays(1).startMillis())

    suspend fun add(amountMl: Int) =
        waterDao.insert(WaterEntry(amountMl = amountMl, dateTime = System.currentTimeMillis()))

    suspend fun undoLast(date: LocalDate) =
        waterDao.deleteLastBetween(date.startMillis(), date.plusDays(1).startMillis())
}
