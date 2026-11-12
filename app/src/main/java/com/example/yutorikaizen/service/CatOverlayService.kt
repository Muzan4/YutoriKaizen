package com.example.yutorikaizen.service

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.IBinder
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.core.app.NotificationCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.RenderMode
import com.example.yutorikaizen.R
import kotlin.math.abs
import kotlin.math.max

/**
 * CatOverlayService — Ultra-efficient floating cat overlay for Yutori Kaizen.
 *
 * Animations:
 *  - R.raw.scroll  : The cute idle cat from Fact (peeks from screen edge)
 *  - R.raw.logocat : The cat typing on a laptop
 *
 * All loading animations have been completely removed.
 */
class CatOverlayService : Service() {

    // ── Window / View state ──────────────────────────────────────────────────
    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private var catView: LottieAnimationView? = null
    private var params: WindowManager.LayoutParams? = null

    // ── Cat animations: ONLY idle cat and cat on laptop ──────────────────────
    private var animationIndex = 0
    private val catAnimations = intArrayOf(R.raw.scroll, R.raw.logocat)

    // ── State flags ──────────────────────────────────────────────────────────
    private var isHidden = false
    private var screenOn = true

    // ── Cached metrics ───────────────────────────────────────────────────────
    private var screenWidth = 0
    private var catSizePx   = 0
    private var visibleTabPx = 0

    // ── Handler for auto-hide, periodic peek, and return to idle cat ─────────
    private val handler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hideOverlay() }
    private val peekStartRunnable = Runnable { startPeek() }
    private val peekStopRunnable  = Runnable { stopPeek()  }

    fun handlePopupClosed() {
        // When shortcut menu closes, show the side overlay back on the side as scroll
        floatingView?.visibility = View.VISIBLE
        switchToIdleCat()
    }

    private fun switchToIdleCat() {
        val p = params ?: return
        val isLeft = p.x + catSizePx / 2 < screenWidth / 2
        catView?.apply {
            setAnimation(R.raw.scroll)
            applyRotation(isLeft)
            repeatCount = 0
            playAnimation()
        }
        resetHideTimer()
    }

    private fun schedulePeek() {
        handler.removeCallbacks(peekStartRunnable)
        handler.removeCallbacks(peekStopRunnable)
        if (isHidden && screenOn) {
            handler.postDelayed(peekStartRunnable, PEEK_INTERVAL_MS)
        }
    }

    private fun startPeek() {
        if (!isHidden || !screenOn) return
        catView?.apply {
            if (isAnimating) return
            speed = 1f
            repeatCount = 0
            playAnimation()
        }
        handler.postDelayed(peekStopRunnable, PEEK_DURATION_MS)
    }

    private fun stopPeek() {
        catView?.pauseAnimation()
        schedulePeek()
    }

    // ── Screen on/off BroadcastReceiver ─────────────────────────────────────
    private val screenReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> onScreenOff()
                Intent.ACTION_SCREEN_ON  -> onScreenOn()
            }
        }
    }

    private fun onScreenOff() {
        screenOn = false
        handler.removeCallbacksAndMessages(null)
        catView?.pauseAnimation()
    }

    private fun onScreenOn() {
        screenOn = true
        if (isHidden) {
            schedulePeek()
        } else {
            catView?.resumeAnimation()
            resetHideTimer()
        }
    }

    // ── SharedPrefs live-update ──────────────────────────────────────────────
    private val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "cat_auto_hide") {
            if (CatOverlayPreferences.isAutoHideEnabled(this)) resetHideTimer()
            else { handler.removeCallbacks(hideRunnable); showOverlay() }
        }
    }

    private var autoHideEnabled = true

    // ────────────────────────────────────────────────────────────────────────
    // Service lifecycle
    // ────────────────────────────────────────────────────────────────────────

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForeground(NOTIFICATION_ID, buildNotification())

        val dm = resources.displayMetrics
        screenWidth  = dm.widthPixels
        catSizePx    = (CAT_SIZE_DP * dm.density).toInt()
        visibleTabPx = (VISIBLE_TAB_DP * dm.density).toInt()

        autoHideEnabled = CatOverlayPreferences.isAutoHideEnabled(this)
        CatOverlayPreferences.registerListener(this, prefListener)

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(screenReceiver, filter)

        setupFloatingView()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (floatingView != null && isHidden) showOverlay()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) instance = null
        handler.removeCallbacksAndMessages(null)
        try { unregisterReceiver(screenReceiver) } catch (_: Exception) {}
        CatOverlayPreferences.unregisterListener(this, prefListener)
        slideAnimator?.cancel()
        slideAnimator = null
        catView?.cancelAnimation()
        floatingView?.let {
            try { windowManager.removeView(it) } catch (_: Exception) {}
        }
        floatingView = null
        catView = null
    }

    // ────────────────────────────────────────────────────────────────────────
    // Floating view setup
    // ────────────────────────────────────────────────────────────────────────

    private fun setupFloatingView() {
        if (floatingView != null) return

        floatingView = LayoutInflater.from(this).inflate(R.layout.overlay_cat_fab, null)
        catView = floatingView!!.findViewById(R.id.cat_lottie)

        val savedSide = CatOverlayPreferences.getLastXSide(this)
        val isLeft = (savedSide == "left")

        catView?.apply {
            setRenderMode(RenderMode.SOFTWARE)
            repeatCount = 0
            setAnimation(catAnimations[animationIndex])
            enableMergePathsForKitKatAndAbove(true)
            applyRotation(isLeft)
        }

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        val savedY = CatOverlayPreferences.getLastY(this)
        val startX = if (isLeft) 0 else screenWidth - catSizePx

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = startX
            y = savedY
        }

        windowManager.addView(floatingView, params)

        catView?.setOnTouchListener(makeTouchListener())
        catView?.setOnClickListener { onCatClick() }

        // Greet user with single animation play
        catView?.playAnimation()

        resetHideTimer()
    }

    /**
     * scroll.json is designed to peek inward from the edge:
     * Left edge: 90f (faces right into screen)
     * Right edge: 270f (faces left into screen)
     */
    private fun applyRotation(isLeft: Boolean) {
        catView?.rotation = if (isLeft) 90f else 270f
    }

    // ────────────────────────────────────────────────────────────────────────
    // Touch: drag + snap + tap detection
    // ────────────────────────────────────────────────────────────────────────

    private fun makeTouchListener() = object : View.OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (isHidden) {
                        showOverlay()
                        return true
                    }
                    resetHideTimer()
                    initialX = params!!.x
                    initialY = params!!.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isHidden) return false
                    params!!.x = initialX + (event.rawX - initialTouchX).toInt()
                    params!!.y = initialY + (event.rawY - initialTouchY).toInt()
                    safeUpdateLayout()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    val diffX = abs(event.rawX - initialTouchX)
                    val diffY = abs(event.rawY - initialTouchY)
                    if (max(diffX, diffY) < 15f) {
                        v.performClick()
                    } else {
                        CatOverlayPreferences.setLastY(this@CatOverlayService, params!!.y)
                        snapToNearestEdge()
                    }
                    return true
                }
            }
            return false
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Click: open shortcut menu (logocat will be in the middle of the menu)
    // ────────────────────────────────────────────────────────────────────────

    private fun onCatClick() {
        handler.removeCallbacks(hideRunnable)

        // Hide side overlay view while menu is open
        floatingView?.visibility = View.GONE

        // Open the transparent shortcut menu in an isolated task
        val intent = Intent(this, CatQuickMenuActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
                Intent.FLAG_ACTIVITY_NO_ANIMATION or
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
            )
        }
        startActivity(intent)
    }

    // ────────────────────────────────────────────────────────────────────────
    // Auto-hide / show mechanics
    // ────────────────────────────────────────────────────────────────────────

    private fun resetHideTimer() {
        handler.removeCallbacks(hideRunnable)
        autoHideEnabled = CatOverlayPreferences.isAutoHideEnabled(this)
        if (autoHideEnabled && !isHidden && screenOn) {
            handler.postDelayed(hideRunnable, HIDE_DELAY_MS)
        }
    }

    private fun snapToNearestEdge() {
        val p = params ?: return
        val isLeft = p.x + catSizePx / 2 < screenWidth / 2
        CatOverlayPreferences.setLastXSide(this, if (isLeft) "left" else "right")
        applyRotation(isLeft)
        animateX(if (isLeft) 0 else screenWidth - catSizePx)
    }

    private fun hideOverlay() {
        if (!autoHideEnabled || isHidden || params == null) return

        val isLeft = params!!.x + catSizePx / 2 < screenWidth / 2
        val slideTarget = if (isLeft) -(catSizePx - visibleTabPx) else screenWidth - visibleTabPx

        animateX(slideTarget) {
            isHidden = true
            safeUpdateLayout()
            if (screenOn) schedulePeek()
        }
    }

    private fun showOverlay() {
        if (!isHidden || params == null) return

        handler.removeCallbacks(peekStartRunnable)
        handler.removeCallbacks(peekStopRunnable)

        val isLeft = params!!.x < screenWidth / 2
        applyRotation(isLeft)

        catView?.apply {
            repeatCount = 0
            playAnimation()
        }

        val targetX = if (isLeft) 0 else screenWidth - catSizePx
        animateX(targetX) {
            isHidden = false
            resetHideTimer()
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // ValueAnimator (slide in/out)
    // ────────────────────────────────────────────────────────────────────────

    private var slideAnimator: ValueAnimator? = null

    private fun animateX(targetX: Int, onEnd: (() -> Unit)? = null) {
        slideAnimator?.cancel()
        slideAnimator = ValueAnimator.ofInt(params!!.x, targetX).apply {
            duration = SLIDE_DURATION_MS
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                params!!.x = it.animatedValue as Int
                safeUpdateLayout()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onEnd?.invoke()
                }
            })
            start()
        }
    }

    private fun safeUpdateLayout() {
        try { windowManager.updateViewLayout(floatingView, params) } catch (_: Exception) {}
    }

    // ────────────────────────────────────────────────────────────────────────
    // Notification (Low priority foreground service)
    // ────────────────────────────────────────────────────────────────────────

    private fun buildNotification(): Notification {
        val channelId = "cat_overlay_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Cat Overlay", NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Yutori cat floating shortcut"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val openIntent = PendingIntent.getActivity(
            this, 0,
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Yutori Cat 🐱")
            .setContentText("Tap for quick shortcuts")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .setOngoing(true)
            .setShowWhen(false)
            .setContentIntent(openIntent)
            .build()
    }

    companion object {
        private const val NOTIFICATION_ID   = 2025
        private const val HIDE_DELAY_MS     = 4_000L   // 4s idle -> hide
        private const val PEEK_INTERVAL_MS  = 30_000L  // peek wiggle every 30s
        private const val PEEK_DURATION_MS  = 2_500L
        private const val SLIDE_DURATION_MS = 300L
        private const val CAT_SIZE_DP       = 80f
        private const val VISIBLE_TAB_DP    = 24f      // comfortable 24dp tab on edge

        @Volatile
        private var instance: CatOverlayService? = null

        fun notifyPopupClosed() {
            instance?.handlePopupClosed()
        }

        fun start(context: Context) {
            val intent = Intent(context, CatOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intent)
            else
                context.startService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, CatOverlayService::class.java))
        }
    }
}
