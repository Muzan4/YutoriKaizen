package com.example.yutorikaizen.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.yutorikaizen.data.local.entity.AppUsageEntity

@Dao
interface AppUsageDao {
    @Upsert
    suspend fun upsert(entity: AppUsageEntity)

    @Query("SELECT * FROM app_usage_events WHERE epochDay >= :startEpochDay AND epochDay <= :endEpochDay")
    suspend fun getForDateRange(startEpochDay: Long, endEpochDay: Long): List<AppUsageEntity>

    @Query("SELECT * FROM app_usage_events WHERE epochDay = :epochDay ORDER BY totalForegroundMs DESC LIMIT :limit")
    suspend fun getTopAppsForDay(epochDay: Long, limit: Int): List<AppUsageEntity>

    @Query("SELECT SUM(totalForegroundMs) FROM app_usage_events WHERE epochDay = :epochDay")
    suspend fun getTotalScreenTimeForDay(epochDay: Long): Long?

    @Query("DELETE FROM app_usage_events WHERE epochDay < :epochDay")
    suspend fun deleteOlderThan(epochDay: Long)

    @Query("DELETE FROM app_usage_events WHERE epochDay = :epochDay")
    suspend fun deleteAllForDay(epochDay: Long)

    @Query("DELETE FROM app_usage_events WHERE totalForegroundMs > 86400000")
    suspend fun deleteCorruptedRecords()

    @Query("SELECT * FROM app_usage_events WHERE epochDay = :epochDay")
    suspend fun getForDay(epochDay: Long): List<AppUsageEntity>
}
