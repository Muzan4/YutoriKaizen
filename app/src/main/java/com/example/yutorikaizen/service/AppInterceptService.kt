package com.example.yutorikaizen.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.yutorikaizen.data.local.dao.InterceptConfigDao
import com.example.yutorikaizen.data.local.db.AppDatabase
import com.example.yutorikaizen.data.local.entity.InterceptConfigEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * AppInterceptService — Fast, zero-lag "One Sec" accessibility interceptor.
 *
 * HOW IT ACHIEVES INSTANT POPUP & MULTI-OPEN SUPPORT:
 * 1. Synchronous in-memory cache: All enabled intercept rules are cached in
 *    a ConcurrentHashMap [rulesCache].
 * 2. Real-time DB sync: A Flow collector actively keeps [rulesCache] updated
 *    whenever rules are added, edited, or toggled in settings.
 * 3. Zero-delay execution: On TYPE_WINDOW_STATE_CHANGED, [rulesCache] is checked
 *    synchronously in O(1) time without waiting for any background coroutines or DB IO.
 * 4. Closing & Reopening handling:
 *    - Tracks [lastForegroundPkg]. When user switches away from the intercepted app
 *      (e.g., goes home, opens launcher, switches apps), [userHasLeftInterceptedApp] is set.
 *    - When the user opens the app again, it intercepts immediately every single time.
 *    - When user explicitly taps "Open Anyway", [tempBypassedPkg] is set briefly so
 *      it doesn't loop on the immediate launch, but resets as soon as they leave.
 */
@AndroidEntryPoint
class AppInterceptService : AccessibilityService() {

    @Inject
    lateinit var db: AppDatabase

    private val dao: InterceptConfigDao by lazy { db.interceptConfigDao() }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        /** In-memory cache of package -> InterceptConfigEntity for instant zero-latency lookups */
        private val rulesCache = ConcurrentHashMap<String, InterceptConfigEntity>()

        /** Package currently allowed to open because user passed the countdown */
        @Volatile
        var tempBypassedPkg: String? = null

        /** Tracks currently active foreground package */
        @Volatile
        var currentForegroundPkg: String? = null
    }

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            packageNames = null // Watch all apps
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 0L // Zero buffer delay for immediate responsiveness
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        }

        // Keep rulesCache in sync in real-time
        serviceScope.launch(Dispatchers.IO) {
            dao.observeAll().collect { list ->
                val activeMap = list.filter { it.enabled }.associateBy { it.packageName }
                rulesCache.clear()
                rulesCache.putAll(activeMap)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        // Ignore our own app
        if (pkg == packageName) {
            currentForegroundPkg = pkg
            return
        }

        // If user navigated away from the bypassed app (e.g. went to launcher or another app),
        // reset the bypass so opening it again will trigger the popup immediately!
        if (tempBypassedPkg != null && pkg != tempBypassedPkg) {
            tempBypassedPkg = null
        }

        // Update foreground tracker
        val previousPkg = currentForegroundPkg
        currentForegroundPkg = pkg

        // If this package is in the rules cache
        val config = rulesCache[pkg] ?: return

        // If user already passed countdown and is actively launching this app, allow it
        if (pkg == tempBypassedPkg) {
            return
        }

        // If the same window event fires repeatedly while already in the app, ignore repeated events
        if (pkg == previousPkg && com.example.yutorikaizen.MainActivity.activeIntercept.value?.packageName == pkg) {
            return
        }

        // Launch intercept popup instantly!
        launchIntercept(config)
    }

    private fun launchIntercept(config: InterceptConfigEntity) {
        val intent = Intent(this, com.example.yutorikaizen.MainActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            )
            putExtra(com.example.yutorikaizen.MainActivity.EXTRA_IS_INTERCEPT, true)
            putExtra(com.example.yutorikaizen.MainActivity.EXTRA_INTERCEPT_PKG, config.packageName)
            putExtra(com.example.yutorikaizen.MainActivity.EXTRA_INTERCEPT_LABEL, config.appLabel)
            putExtra(com.example.yutorikaizen.MainActivity.EXTRA_INTERCEPT_PROMPT, config.prompt)
            putExtra(com.example.yutorikaizen.MainActivity.EXTRA_INTERCEPT_DELAY, config.delaySeconds)
        }
        startActivity(intent)
    }

    override fun onInterrupt() { /* no-op */ }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        rulesCache.clear()
        tempBypassedPkg = null
        currentForegroundPkg = null
    }
}
