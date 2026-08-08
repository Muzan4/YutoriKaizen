package com.example.yutorikaizen.presentation.screens.duckbreak

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
 * Duck Break screen placeholder — Phase 0.
 * Full implementation (breathing, journal, physical nudge): Phase 4.
 */
@Composable
fun DuckBreakScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DuckSprite(
            duckState   = DuckState.CONTENT,
            isBreathing = true,
            size        = 140.dp
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text  = "Duck Break",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text  = "Breathe, write, or move — coming in Phase 4",
            style = MaterialTheme.typography.bodyMedium,
            color = MoonGray
        )
    }
}
