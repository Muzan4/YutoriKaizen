package com.example.yutorikaizen.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yutorikaizen.domain.model.DuckState
import com.example.yutorikaizen.presentation.components.DuckSprite
import com.example.yutorikaizen.presentation.theme.DeepNight
import com.example.yutorikaizen.presentation.theme.MoonGray

/**
 * Home screen placeholder — Phase 0.
 *
 * Shows the duck in CONTENT state so the Canvas animation is visible
 * immediately. The full HomeScreen (Deliverable 6) is wired in Phase 3.
 */
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DuckSprite(
            duckState = DuckState.CONTENT,
            size      = 140.dp
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text  = "Yutori Kaizen",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text  = "Your focus companion is ready 🌙",
            style = MaterialTheme.typography.bodyMedium,
            color = MoonGray
        )
    }
}
