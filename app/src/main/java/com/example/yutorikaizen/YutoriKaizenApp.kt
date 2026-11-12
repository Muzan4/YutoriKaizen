package com.example.yutorikaizen

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.yutorikaizen.data.local.UsageSyncWorker
import com.example.yutorikaizen.service.DailyReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

/**
 * Application class annotated with @HiltAndroidApp.
 *
 * WHY: Hilt needs to generate a component tree at compile time. This annotation
 * tells Hilt to use this class as the root of that tree. Without it, @Inject
 * and @HiltViewModel won't work anywhere in the app. It MUST be registered in
 * the manifest via android:name=".YutoriKaizenApp".
 */
@HiltAndroidApp
class YutoriKaizenApp : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleUsageSyncWork()
        DailyReminderScheduler.scheduleDailyReminders(this)
    }

    private fun scheduleUsageSyncWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<UsageSyncWorker>(2, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "usage_sync_work",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
