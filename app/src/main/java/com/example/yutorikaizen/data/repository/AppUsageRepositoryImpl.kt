package com.example.yutorikaizen.data.repository

import com.example.yutorikaizen.data.local.dao.AppUsageDao
import com.example.yutorikaizen.data.local.entity.AppUsageEntity
import com.example.yutorikaizen.domain.model.AppUsage
import com.example.yutorikaizen.domain.repository.AppUsageRepository
import javax.inject.Inject

class AppUsageRepositoryImpl @Inject constructor(
    private val dao: AppUsageDao
) : AppUsageRepository {

    override suspend fun upsert(appUsage: AppUsage) {
        dao.upsert(appUsage.toEntity())
    }

    override suspend fun getForDateRange(startEpochDay: Long, endEpochDay: Long): List<AppUsage> {
        return dao.getForDateRange(startEpochDay, endEpochDay).map { it.toDomain() }
    }

    override suspend fun getTopAppsForDay(epochDay: Long, limit: Int): List<AppUsage> {
        return dao.getTopAppsForDay(epochDay, limit).map { it.toDomain() }
    }

    override suspend fun getTotalScreenTimeForDay(epochDay: Long): Long? {
        return dao.getTotalScreenTimeForDay(epochDay)
    }

    override suspend fun deleteOlderThan(epochDay: Long) {
        dao.deleteOlderThan(epochDay)
    }

    override suspend fun getForDay(epochDay: Long): List<AppUsage> {
        return dao.getForDay(epochDay).map { it.toDomain() }
    }

    private fun AppUsageEntity.toDomain() = AppUsage(
        packageName = packageName,
        appLabel = appLabel,
        epochDay = epochDay,
        totalForegroundMinutes = (totalForegroundMs.coerceAtMost(24 * 60 * 60 * 1000L) / 60000).toInt(),
        launchCount = launchCount
    )

    private fun AppUsage.toEntity() = AppUsageEntity(
        packageName = packageName,
        appLabel = appLabel,
        epochDay = epochDay,
        totalForegroundMs = totalForegroundMinutes * 60000L,
        launchCount = launchCount
    )
}
