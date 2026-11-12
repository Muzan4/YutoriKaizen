package com.example.yutorikaizen.domain.repository

import com.example.yutorikaizen.domain.model.AppUsage

interface AppUsageRepository {
    suspend fun upsert(appUsage: AppUsage)
    suspend fun getForDateRange(startEpochDay: Long, endEpochDay: Long): List<AppUsage>
    suspend fun getTopAppsForDay(epochDay: Long, limit: Int): List<AppUsage>
    suspend fun getTotalScreenTimeForDay(epochDay: Long): Long?
    suspend fun deleteOlderThan(epochDay: Long)
    suspend fun getForDay(epochDay: Long): List<AppUsage>
}
