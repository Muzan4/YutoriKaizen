package com.example.yutorikaizen.presentation.screens.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.presentation.theme.*

@Composable
fun FocusTimerScreen(
    viewModel: FocusTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val minutes  = uiState.secondsRemaining / 60
    val seconds  = uiState.secondsRemaining % 60
    val progress = if (uiState.durationSeconds > 0)
        uiState.secondsRemaining.toFloat() / uiState.durationSeconds.toFloat()
    else 1f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyBlack)
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Status chip
        Surface(
            color = CozyBlack4,
            shape = RoundedCornerShape(24.dp)
        ) {
            Text(
                text  = when {
                    uiState.isRunning -> "Focusing… 🧘"
                    uiState.isPaused  -> "Paused ⏸"
                    else              -> "Ready to focus? 🐸"
                },
                style  = MaterialTheme.typography.labelLarge,
                color  = SoftTan,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(40.dp))

        // Progress ring + time
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier.size(240.dp)
        ) {
            CircularProgressIndicator(
                progress    = { progress },
                modifier    = Modifier.fillMaxSize(),
                color       = MossGreen,
                strokeWidth = 10.dp,
                trackColor  = CozyBlack3,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = String.format("%02d:%02d", minutes, seconds),
                    style      = MaterialTheme.typography.displayLarge,
                    color      = CreamWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text  = "minutes",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftTan,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(Modifier.height(52.dp))

        // Controls
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            when {
                !uiState.isRunning && !uiState.isPaused -> {
                    Button(
                        onClick  = { viewModel.startTimer(25) },
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MossGreen,
                            contentColor   = CozyBlack
                        ),
                        shape    = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(56.dp).weight(1f)
                    ) {
                        Text("▶  Start Session", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
                uiState.isRunning -> {
                    Button(
                        onClick  = { viewModel.pauseTimer() },
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = CozyAmber,
                            contentColor   = CozyBlack
                        ),
                        shape    = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(56.dp).weight(1f)
                    ) {
                        Text("⏸  Pause", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
                uiState.isPaused -> {
                    Button(
                        onClick  = { viewModel.resumeTimer() },
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MossGreen,
                            contentColor   = CozyBlack
                        ),
                        shape    = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(56.dp).weight(1.2f)
                    ) {
                        Text("▶  Resume", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick  = { viewModel.stopTimer() },
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = WarmCream),
                        border   = androidx.compose.foundation.BorderStroke(1.5.dp, CozyBorder),
                        shape    = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(56.dp).weight(0.8f)
                    ) {
                        Text("Reset", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
