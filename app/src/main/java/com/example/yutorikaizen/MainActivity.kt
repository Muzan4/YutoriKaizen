package com.example.yutorikaizen

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import com.example.yutorikaizen.data.local.SkinPreferences
import com.example.yutorikaizen.presentation.navigation.AppNavGraph
import com.example.yutorikaizen.presentation.screens.intercept.InterceptOverlayScreen
import com.example.yutorikaizen.presentation.theme.YutoriKaizenTheme
import com.example.yutorikaizen.service.DailyReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import org.godotengine.godot.Godot
import org.godotengine.godot.GodotHost
import javax.inject.Inject

data class ActiveInterceptData(
    val packageName: String,
    val appLabel: String,
    val prompt: String,
    val delaySeconds: Int
)

/**
 * MainActivity — Hosts the Godot Engine (which acts as a [Fragment] in Godot 3.4)
 * as a background layer, and overlays Jetpack Compose screens on top.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity(), GodotHost {

    @Inject lateinit var skinPreferences: SkinPreferences

    private var godotFragment: Godot? = null

    companion object {
        const val EXTRA_IS_INTERCEPT = "extra_is_intercept"
        const val EXTRA_INTERCEPT_PKG = "extra_intercept_pkg"
        const val EXTRA_INTERCEPT_LABEL = "extra_intercept_label"
        const val EXTRA_INTERCEPT_PROMPT = "extra_intercept_prompt"
        const val EXTRA_INTERCEPT_DELAY = "extra_intercept_delay"
        const val EXTRA_NAVIGATE_TO = "extra_navigate_to"

        val activeIntercept = MutableStateFlow<ActiveInterceptData?>(null)
        val pendingNavigation = MutableStateFlow<String?>(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleInterceptIntent(intent)
        handleNavigationIntent(intent)
        DailyReminderScheduler.scheduleDailyReminders(this)
        requestNotificationPermissionIfNeeded()

        // 1. Create a root FrameLayout to contain both layers
        val rootLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // 2. Add container for the Godot Fragment (base background layer)
        val godotContainerId = android.view.View.generateViewId()
        val godotContainer = FrameLayout(this).apply {
            id = godotContainerId
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            isFocusable = false
            isFocusableInTouchMode = false
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        }
        rootLayout.addView(godotContainer)

        // 3. Add ComposeView for our app's screens (foreground layer)
        val composeView = ComposeView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
            setContent {
                YutoriKaizenTheme {
                    val intercept by activeIntercept.collectAsState()
                    if (intercept != null) {
                        InterceptOverlayScreen(
                            appLabel = intercept!!.appLabel,
                            prompt = intercept!!.prompt,
                            delaySeconds = intercept!!.delaySeconds,
                            targetPackage = intercept!!.packageName,
                            onOpenApp = {
                                activeIntercept.value = null
                            },
                            onDontOpen = {
                                activeIntercept.value = null
                                val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                                    addCategory(Intent.CATEGORY_HOME)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                startActivity(homeIntent)
                            }
                        )
                    } else {
                        AppNavGraph()
                    }
                }
            }
        }
        rootLayout.addView(composeView)

        setContentView(rootLayout)

        // 4. Initialize and display the Godot Fragment (using org.godotengine.godot.Godot)
        val currentFragment = supportFragmentManager.findFragmentById(godotContainerId)
        if (currentFragment is Godot) {
            godotFragment = currentFragment
        } else {
            godotFragment = Godot()
            supportFragmentManager.beginTransaction()
                .replace(godotContainerId, godotFragment!!)
                .commitNowAllowingStateLoss()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleInterceptIntent(intent)
        handleNavigationIntent(intent)
    }

    private fun handleNavigationIntent(intent: Intent?) {
        val dest = intent?.getStringExtra(EXTRA_NAVIGATE_TO)
        if (!dest.isNullOrBlank()) {
            activeIntercept.value = null
            pendingNavigation.value = null
            pendingNavigation.value = dest
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(permission), 2001)
            }
        }
    }

    private fun handleInterceptIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_IS_INTERCEPT, false) == true) {
            val pkg = intent.getStringExtra(EXTRA_INTERCEPT_PKG) ?: return
            val label = intent.getStringExtra(EXTRA_INTERCEPT_LABEL) ?: pkg
            val prompt = intent.getStringExtra(EXTRA_INTERCEPT_PROMPT) ?: "Pause for a moment."
            val delay = intent.getIntExtra(EXTRA_INTERCEPT_DELAY, 5)
            activeIntercept.value = ActiveInterceptData(
                packageName = pkg,
                appLabel = label,
                prompt = prompt,
                delaySeconds = delay
            )
        }
    }

    /**
     * Provide custom command-line arguments to the Godot engine at startup.
     * Tells Godot to load the bundled frog.pck file and which skin .tres to use.
     */
    override fun getCommandLine(): List<String> {
        val skinId = skinPreferences.getSelectedSkinIdBlocking()
        return listOf("--main-pack", "res://frog.pck", "--skin", skinId)
    }

    /**
     * Because Jetpack Compose sits completely on top of Godot, it normally intercepts and consumes
     * all touch events. We override dispatchTouchEvent here to forward touches down into the Godot
     * layer as well, allowing the background game to remain interactive!
     */
    override fun dispatchTouchEvent(ev: android.view.MotionEvent?): Boolean {
        if (ev != null) {
            val screenHeight = resources.displayMetrics.heightPixels
            
            // Forward touch to Godot when user taps around the middle
            val isTopUiArea = ev.rawY < screenHeight * 0.15f
            val isBottomUiArea = ev.rawY > screenHeight * 0.75f
            
            if (!isTopUiArea && !isBottomUiArea) {
                val godotEvent = android.view.MotionEvent.obtain(ev)
                godotFragment?.view?.dispatchTouchEvent(godotEvent)
                godotEvent.recycle()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * Intercept the phone's bottom navigation bar back button (KEYCODE_BACK) at window level.
     * Prevents Godot's GLSurfaceView from swallowing KEYCODE_BACK key events so Compose's
     * OnBackPressedDispatcher and BackHandlers always trigger reliably!
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_UP) {
                if (activeIntercept.value != null) {
                    activeIntercept.value = null
                    val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_HOME)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(homeIntent)
                } else {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (activeIntercept.value != null) {
                activeIntercept.value = null
                val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(homeIntent)
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    override fun onBackPressed() {
        if (activeIntercept.value != null) {
            activeIntercept.value = null
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(homeIntent)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}