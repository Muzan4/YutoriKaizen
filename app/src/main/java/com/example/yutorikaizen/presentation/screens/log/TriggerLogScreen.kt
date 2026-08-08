package com.example.yutorikaizen.presentation.screens.log

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
import com.example.yutorikaizen.presentation.theme.DeepNight
import com.example.yutorikaizen.presentation.theme.MoonGray

/**
 * Trigger Log screen placeholder — Phase 0.
 * Full implementation with emotion chips, note field, and weekly pattern: Phase 4.
 */
@Composable
fun TriggerLogScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🪵", style = MaterialTheme.typography.displayLarge)

        Spacer(Modifier.height(16.dp))

        Text(
            text  = "Urge Log",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text  = "Log what you're feeling — coming in Phase 4",
            style = MaterialTheme.typography.bodyMedium,
            color = MoonGray
        )
    }
}
