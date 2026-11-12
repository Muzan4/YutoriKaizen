package com.example.yutorikaizen.data.local

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.yutorikaizen.data.local.db.AppDatabase
import com.example.yutorikaizen.di.UsageSyncEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDate

class UsageSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            UsageSyncEntryPoint::class.java
        )
        
        val usageTracker = entryPoint.usageTracker()
        val appUsageDao = entryPoint.appUsageDao()

        if (!usageTracker.hasPermission()) {
            return Result.failure()
        }

        try {
            usageTracker.syncUsageForToday()
            
            // Delete records older than 30 days
            val threshold = LocalDate.now().minusDays(30).toEpochDay()
            appUsageDao.deleteOlderThan(threshold)
            
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }
}
