package com.example.yutorikaizen.presentation.screens.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.domain.model.DuckState
import com.example.yutorikaizen.presentation.components.DuckSprite
import com.example.yutorikaizen.presentation.theme.*

/**
 * FocusTimerScreen — UI for the countdown timer.
 *
 * WHY CircularProgressIndicator?
 * A circular track provides a clean, visual representation of remaining time that
 * can be absorbed in a split second, letting the user gauge progress without
 * reading numbers.
 *
 * WHY collectAsStateWithLifecycle?
 * Collects flows in a lifecycle-aware manner. When the screen goes to background,
 * flow collection is stopped, saving precious battery and CPU cycles.
 */
@Composable
fun FocusTimerScreen(
    viewModel: FocusTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val minutes = uiState.secondsRemaining / 60
    val seconds = uiState.secondsRemaining % 60
    
    // Smooth progress representation
    val progress = if (uiState.durationSeconds > 0) {
        uiState.secondsRemaining.toFloat() / uiState.durationSeconds.toFloat()
    } else 1f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Mascot + Ring
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(240.dp)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(220.dp),
                color = CalmTeal,
                strokeWidth = 12.dp,
                trackColor = RingTrack,
            )
            // Shows working pencil accessory when timer is active
            DuckSprite(
                duckState = DuckState.CONTENT,
                isWorking  = uiState.isRunning,
                size      = 110.dp
            )
        }

        Spacer(Modifier.height(32.dp))

        // Time display
        Text(
            text  = String.format("%02d:%02d", minutes, seconds),
            style = MaterialTheme.typography.displayLarge,
            color = StarWhite
        )

        Spacer(Modifier.height(48.dp))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when {
                // Timer is inactive: show Start button
                !uiState.isRunning && !uiState.isPaused -> {
                    Button(
                        onClick = { viewModel.startTimer(25) },
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = CalmTeal,
                            contentColor   = DeepNight
                        ),
                        shape    = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text  = "Start Session",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                
                // Timer is running: show Pause button
                uiState.isRunning -> {
                    Button(
                        onClick = { viewModel.pauseTimer() },
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = DuckAmber,
                            contentColor   = DeepNight
                        ),
                        shape    = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text  = "Pause",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
                
                // Timer is paused: show Resume and Reset buttons
                uiState.isPaused -> {
                    Button(
                        onClick = { viewModel.resumeTimer() },
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = CalmTeal,
                            contentColor   = DeepNight
                        ),
                        shape    = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1.2f)
                    ) {
                        Text(
                            text  = "Resume",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    
                    OutlinedButton(
                        onClick  = { viewModel.stopTimer() },
                        colors   = ButtonDefaults.outlinedButtonColors(
                            contentColor = StarWhite
                        ),
                        border   = androidx.compose.foundation.BorderStroke(2.dp, NightBorder),
                        shape    = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .weight(0.8f)
                    ) {
                        Text(
                            text  = "Reset",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
