package com.example.yutorikaizen.data.local

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Process
import android.provider.Settings
import com.example.yutorikaizen.data.local.dao.AppUsageDao
import com.example.yutorikaizen.data.local.entity.AppUsageEntity
import com.example.yutorikaizen.domain.model.SocialMediaApps
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appUsageDao: AppUsageDao
) {

    fun hasPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getRequestPermissionIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    suspend fun syncUsageForToday() {
        if (!hasPermission()) return

        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val pm = context.packageManager
        
        val launchableCache = mutableMapOf<String, Boolean>()
        
        val ignoredPackages = setOf(
            "com.android.deskclock", "com.google.android.deskclock", "com.sec.android.app.clockpackage",
            "com.android.calculator2", "com.google.android.calculator", "com.sec.android.app.popupcalculator",
            "com.android.camera", "com.google.android.GoogleCamera", "com.sec.android.app.camera", "com.apple.camera",
            "com.android.settings", "com.android.phone", "com.android.server.telecom", "com.google.android.dialer", "com.samsung.android.dialer",
            "com.android.mms", "com.google.android.apps.messaging", "com.samsung.android.messaging",
            "com.android.contacts", "com.google.android.contacts", "com.samsung.android.app.contacts",
            "com.google.android.googlequicksearchbox", "com.android.vending", "com.sec.android.app.launcher"
        )
        
        fun isLaunchableAndValid(packageName: String): Boolean {
            if (packageName == context.packageName) return false // Skip our own app
            if (packageName in ignoredPackages) return false
            if (packageName.contains("systemui")) return false
            if (packageName.contains("launcher")) return false
            return launchableCache.getOrPut(packageName) {
                pm.getLaunchIntentForPackage(packageName) != null
            }
        }

        val today = LocalDate.now()
        val epochDay = today.toEpochDay()
        val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = System.currentTimeMillis() // Current time today
        val maxDurationToday = (endOfDay - startOfDay).coerceAtLeast(0L) // Can NEVER exceed time elapsed today!

        // 1. First, fetch the official Android OS daily bucket usage stats.
        // Android's internal UsageStatsService is the ground truth because it tracks
        // power manager, screen off, and window focus natively without missing pause events.
        val osDailyUsage = mutableMapOf<String, Long>()
        try {
            val dailyStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startOfDay,
                endOfDay
            )
            if (!dailyStats.isNullOrEmpty()) {
                for (usage in dailyStats) {
                    val pkg = usage.packageName ?: continue
                    if (!isLaunchableAndValid(pkg)) continue
                    if (usage.lastTimeUsed >= startOfDay) {
                        val fgTime = usage.totalTimeInForeground.coerceIn(0L, maxDurationToday)
                        if (fgTime > 0) {
                            osDailyUsage[pkg] = maxOf(osDailyUsage[pkg] ?: 0L, fgTime)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore daily query errors
        }

        // 2. Process UsageEvents to calculate launch counts and fine-grained event durations.
        val usageEvents = usageStatsManager.queryEvents(startOfDay, endOfDay)
        val event = UsageEvents.Event()
        
        val appStats = mutableMapOf<String, AppUsageStat>()
        var currentForegroundPkg: String? = null
        var currentForegroundStart: Long = 0L

        fun closeForegroundSession(timestamp: Long) {
            val pkg = currentForegroundPkg ?: return
            if (currentForegroundStart > 0 && timestamp > currentForegroundStart) {
                val sessionStart = maxOf(currentForegroundStart, startOfDay)
                val duration = timestamp - sessionStart
                // Only credit if it's a realistic active session (under 2 hours for a single continuous screen-on session)
                if (duration in 1..maxDurationToday) {
                    val stat = appStats.getOrPut(pkg) { AppUsageStat(pkg) }
                    stat.totalForegroundMs += duration
                }
            }
            currentForegroundPkg = null
            currentForegroundStart = 0L
        }

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            val packageName = event.packageName

            when (event.eventType) {
                UsageEvents.Event.ACTIVITY_RESUMED -> {
                    val eventTime = event.timeStamp
                    // Any newly resumed app (including launcher or system UI) closes the previous app!
                    closeForegroundSession(eventTime)

                    if (packageName != null && isLaunchableAndValid(packageName)) {
                        currentForegroundPkg = packageName
                        currentForegroundStart = eventTime
                        val stat = appStats.getOrPut(packageName) { AppUsageStat(packageName) }
                        stat.launchCount++
                    }
                }

                UsageEvents.Event.ACTIVITY_PAUSED -> {
                    val eventTime = event.timeStamp
                    if (packageName != null && packageName == currentForegroundPkg) {
                        closeForegroundSession(eventTime)
                    }
                }

                UsageEvents.Event.SCREEN_NON_INTERACTIVE,
                UsageEvents.Event.KEYGUARD_SHOWN -> {
                    // Screen turned off or phone locked -> close active session immediately
                    closeForegroundSession(event.timeStamp)
                }
            }
        }
        
        // If an app was open at the very end of the event stream, cap the dangling session to max 5 minutes
        if (currentForegroundPkg != null && currentForegroundStart > 0) {
            val sessionStart = maxOf(currentForegroundStart, startOfDay)
            val danglingDuration = (endOfDay - sessionStart).coerceIn(0L, 5 * 60 * 1000L)
            if (danglingDuration > 0) {
                val stat = appStats.getOrPut(currentForegroundPkg!!) { AppUsageStat(currentForegroundPkg!!) }
                stat.totalForegroundMs += danglingDuration
            }
        }

        // 3. Reconcile with OS daily usage stats:
        // Merge in any launchable apps found in osDailyUsage that might have been missed by events
        for ((pkg, osTime) in osDailyUsage) {
            if (osTime > 0) {
                val stat = appStats.getOrPut(pkg) { AppUsageStat(pkg) }
                if (stat.launchCount == 0) stat.launchCount = 1
            }
        }

        // Clean up any previously stored corrupt records and wipe today's entries for a clean insert
        appUsageDao.deleteCorruptedRecords()
        appUsageDao.deleteAllForDay(epochDay)
        
        appStats.values.forEach { stat ->
            val pkg = stat.packageName
            val osTime = osDailyUsage[pkg]

            // If the OS daily stats measured this app, use the OS measurement (or the minimum
            // between the event duration and the OS duration) to prevent phantom 10-hour sessions!
            val finalForegroundMs = when {
                osTime != null && osTime > 0 -> {
                    if (stat.totalForegroundMs > 0) {
                        // Trust the OS ground truth: cap any over-estimated event duration
                        minOf(stat.totalForegroundMs, osTime)
                    } else {
                        osTime
                    }
                }
                else -> stat.totalForegroundMs.coerceIn(0L, maxDurationToday)
            }

            if (finalForegroundMs > 0) {
                val appLabel = try {
                    val appInfo = pm.getApplicationInfo(stat.packageName, 0)
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    SocialMediaApps.getLabel(stat.packageName)
                }
                
                appUsageDao.upsert(
                    AppUsageEntity(
                        packageName = stat.packageName,
                        appLabel = appLabel,
                        epochDay = epochDay,
                        totalForegroundMs = finalForegroundMs,
                        launchCount = stat.launchCount
                    )
                )
            }
        }
    }

    private data class AppUsageStat(
        val packageName: String,
        var totalForegroundMs: Long = 0,
        var launchCount: Int = 0
    )
}
