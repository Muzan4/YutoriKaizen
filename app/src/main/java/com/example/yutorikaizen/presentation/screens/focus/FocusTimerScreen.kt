package com.example.yutorikaizen.presentation.screens.focus

import androidx.compose.animation.*
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.presentation.theme.*

private val DURATION_OPTIONS = listOf(15, 25, 45, 60, 90)

@Composable
fun FocusTimerScreen(
    onBack: () -> Unit = {},
    viewModel: FocusTimerViewModel = hiltViewModel()
) {
    BackHandler(onBack = onBack)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val minutes  = uiState.secondsRemaining / 60
    val seconds  = uiState.secondsRemaining % 60
    val progress = if (uiState.durationSeconds > 0)
        uiState.secondsRemaining.toFloat() / uiState.durationSeconds.toFloat()
    else 1f

    // Selected duration pill state (only matters before session starts)
    var selectedMinutes by remember { mutableIntStateOf(25) }

    // Glow animation while running
    val infiniteTransition = rememberInfiniteTransition(label = "focus_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.08f,
        targetValue   = 0.22f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyBlack),
        contentAlignment = Alignment.Center
    ) {
        // Top back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .background(CozyBlack3.copy(alpha = 0.85f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = CreamWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Status label ─────────────────────────────────────────────────
            AnimatedContent(
                targetState   = when {
                    uiState.isCompleted -> "complete"
                    uiState.isRunning   -> "running"
                    uiState.isPaused    -> "paused"
                    else                -> "idle"
                },
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                label          = "status_anim",
                modifier       = Modifier.height(28.dp)
            ) { status ->
                Text(
                    text = when (status) {
                        "complete" -> "Session complete 🎉"
                        "running"  -> "Deep work in progress"
                        "paused"   -> "Session paused"
                        else       -> "Ready to focus"
                    },
                    style    = MaterialTheme.typography.labelLarge.copy(
                        fontFamily    = Caveat,
                        fontSize      = 20.sp,
                        letterSpacing = 0.sp
                    ),
                    color    = when (status) {
                        "complete" -> MossGreen
                        "running"  -> SoftTan
                        "paused"   -> CozyAmber
                        else       -> DimTan
                    },
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(36.dp))

            // ── Timer ring ───────────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier.size(260.dp)
            ) {
                // Glow when running
                if (uiState.isRunning) {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(MossGreen.copy(alpha = glowAlpha), Color.Transparent)
                                ),
                                CircleShape
                            )
                    )
                }

                // Ring canvas
                val animatedProgress by animateFloatAsState(
                    targetValue   = progress,
                    animationSpec = tween(500, easing = EaseOutCubic),
                    label         = "ring_progress"
                )
                val ringColor = when {
                    uiState.isCompleted -> MossGreen
                    uiState.isPaused    -> CozyAmber
                    else                -> MossGreen
                }

                Canvas(modifier = Modifier.size(260.dp)) {
                    // Track
                    drawArc(
                        color      = CozyBlack3,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter  = false,
                        style      = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // Progress arc
                    if (animatedProgress > 0f) {
                        drawArc(
                            color      = ringColor,
                            startAngle = -90f,
                            sweepAngle = animatedProgress * 360f,
                            useCenter  = false,
                            style      = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                // Center content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (uiState.isCompleted) {
                        Text("✓", fontSize = 52.sp, color = MossGreen, fontWeight = FontWeight.Bold)
                        Text(
                            "Done!",
                            style      = MaterialTheme.typography.headlineMedium,
                            color      = MossGreen,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            text       = String.format("%02d:%02d", minutes, seconds),
                            style      = MaterialTheme.typography.displayLarge.copy(
                                fontSize   = 52.sp,
                                lineHeight = 56.sp
                            ),
                            color      = CreamWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text  = "${uiState.durationSeconds / 60} min session",
                            style = MaterialTheme.typography.labelSmall,
                            color = DimTan
                        )
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            // ── Duration picker (only shown when idle) ───────────────────────
            AnimatedVisibility(
                visible = !uiState.isRunning && !uiState.isPaused && !uiState.isCompleted,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Session length",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = DimTan
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DURATION_OPTIONS.forEach { mins ->
                            val isActive = mins == selectedMinutes
                            Surface(
                                onClick       = { selectedMinutes = mins },
                                color         = if (isActive) MossGreenDim else CozyBlack2,
                                shape         = RoundedCornerShape(10.dp),
                                border        = BorderStroke(
                                    if (isActive) 1.5.dp else 1.dp,
                                    if (isActive) MossGreen else CozyBorder
                                ),
                                modifier      = Modifier.weight(1f)
                            ) {
                                Text(
                                    "${mins}m",
                                    style     = MaterialTheme.typography.labelMedium,
                                    color     = if (isActive) MossGreen else SoftTan,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    textAlign  = TextAlign.Center,
                                    modifier   = Modifier.padding(vertical = 10.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }

            // ── Action buttons ────────────────────────────────────────────────
            when {
                uiState.isCompleted -> {
                    Button(
                        onClick  = { viewModel.startTimer(selectedMinutes) },
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MossGreen,
                            contentColor   = CozyBlack
                        ),
                        shape    = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text("Start another session", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }

                !uiState.isRunning && !uiState.isPaused -> {
                    Button(
                        onClick  = { viewModel.startTimer(selectedMinutes) },
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MossGreen,
                            contentColor   = CozyBlack
                        ),
                        shape    = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp)
                    ) {
                        Text(
                            "Start ${selectedMinutes}-min session",
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        )
                    }
                }

                uiState.isRunning -> {
                    Button(
                        onClick  = { viewModel.pauseTimer() },
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = CozyAmber.copy(alpha = 0.15f),
                            contentColor   = CozyAmber
                        ),
                        shape    = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text("Pause", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }

                uiState.isPaused -> {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick  = { viewModel.resumeTimer() },
                            colors   = ButtonDefaults.buttonColors(
                                containerColor = MossGreen,
                                contentColor   = CozyBlack
                            ),
                            shape    = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(54.dp).weight(1.4f)
                        ) {
                            Text("Resume", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.stopTimer() },
                            colors  = ButtonDefaults.outlinedButtonColors(contentColor = SoftTan),
                            border  = BorderStroke(1.dp, CozyBorder),
                            shape   = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(54.dp).weight(0.8f)
                        ) {
                            Text("Reset", fontSize = 15.sp)
                        }
                    }
                }
            }

            // ── Quiet encouragement tip ───────────────────────────────────────
            if (!uiState.isRunning && !uiState.isCompleted) {
                Spacer(Modifier.height(24.dp))
                Text(
                    when {
                        uiState.isPaused -> "The hardest part is restarting. You can do it."
                        else             -> "Put your phone face down. No notifications allowed."
                    },
                    style     = MaterialTheme.typography.bodySmall,
                    color     = DimTan,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
