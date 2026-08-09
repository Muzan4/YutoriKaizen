package com.example.yutorikaizen

import android.os.Bundle
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentActivity
import com.example.yutorikaizen.presentation.navigation.AppNavGraph
import com.example.yutorikaizen.presentation.theme.YutoriKaizenTheme
import dagger.hilt.android.AndroidEntryPoint
import org.godotengine.godot.Godot
import org.godotengine.godot.GodotHost

/**
 * MainActivity — Hosts the Godot Engine (which acts as a [Fragment] in Godot 3.4)
 * as a background layer, and overlays Jetpack Compose screens on top.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity(), GodotHost {

    private var godotFragment: Godot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        }
        rootLayout.addView(godotContainer)

        // 3. Add ComposeView for our app's screens (foreground layer)
        val composeView = ComposeView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setContent {
                YutoriKaizenTheme {
                    AppNavGraph()
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

    /**
     * Provide custom command-line arguments to the Godot engine at startup.
     * Tells Godot to load the bundled frog.pck file!
     */
    override fun getCommandLine(): List<String> {
        return listOf("--main-pack", "res://frog.pck")
    }

    /**
     * Because Jetpack Compose sits completely on top of Godot, it normally intercepts and consumes
     * all touch events. We override dispatchTouchEvent here to forward touches down into the Godot
     * layer as well, allowing the background game to remain interactive!
     */
    override fun dispatchTouchEvent(ev: android.view.MotionEvent?): Boolean {
        if (ev != null) {
            val screenHeight = resources.displayMetrics.heightPixels
            
            // The top 15% of the screen contains the greeting chips.
            // The bottom 25% of the screen contains the Start/Break buttons and Navigation Bar.
            // We only want the frog to respond if the user taps the empty space in the middle.
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
}