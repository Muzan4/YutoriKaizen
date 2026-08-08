package com.example.yutorikaizen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.AndroidEntryPoint
import com.example.yutorikaizen.presentation.navigation.AppNavGraph
import com.example.yutorikaizen.presentation.theme.DeepNight
import com.example.yutorikaizen.presentation.theme.YutoriKaizenTheme

/**
 * @AndroidEntryPoint — tells Hilt to inject dependencies into this activity.
 *
 * WHY two annotations (@HiltAndroidApp on Application + @AndroidEntryPoint here)?
 * @HiltAndroidApp creates the Hilt component tree once at app start.
 * @AndroidEntryPoint makes this specific component (Activity) a Hilt injection
 * point — without it, any @HiltViewModel inside a screen launched from this
 * activity would fail at runtime.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YutoriKaizenTheme {
                // DeepNight fills behind the system bars (edge-to-edge)
                // so there's never a white flash behind the status bar.
                AppNavGraph()
            }
        }
    }
}