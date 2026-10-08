package ru.watertracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Insert
    suspend fun insert(entry: WaterEntry): Long

    @Query(
        "DELETE FROM water_entries WHERE id = (" +
            "SELECT id FROM water_entries WHERE dateTime >= :from AND dateTime < :to " +
            "ORDER BY dateTime DESC, id DESC LIMIT 1)"
    )
    suspend fun deleteLastBetween(from: Long, to: Long): Int

    @Query("SELECT * FROM water_entries WHERE dateTime >= :from AND dateTime < :to ORDER BY dateTime DESC, id DESC")
    fun observeBetween(from: Long, to: Long): Flow<List<WaterEntry>>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM water_entries WHERE dateTime >= :from AND dateTime < :to")
    suspend fun sumBetween(from: Long, to: Long): Int

    @Query("SELECT * FROM water_entries ORDER BY dateTime")
    fun observeAll(): Flow<List<WaterEntry>>
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1")
    fun observe(): Flow<Settings?>

    @Query("SELECT * FROM settings WHERE id = 1")
    suspend fun get(): Settings?

    @Upsert
    suspend fun save(settings: Settings)
}
