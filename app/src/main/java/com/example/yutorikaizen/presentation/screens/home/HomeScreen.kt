package com.example.yutorikaizen.presentation.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.presentation.theme.*
import com.example.yutorikaizen.presentation.components.tourTarget
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings

/**
 * Home screen — transparent overlay over the Godot frog background.
 * Redesigned to show the dopamine journey phase, NCI score, and Surge button.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onStartFocusSession: () -> Unit = {},
    onDuckBreak: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onSurgeClick: () -> Unit = {},
    onDailyInsightClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Top: greeting + account icon ────────────────────────────────────
        Row(
            modifier              = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(painter = androidx.compose.ui.res.painterResource(id = com.example.yutorikaizen.R.drawable.ic_ribi_head), contentDescription = "Ribi", modifier = Modifier.size(20.dp).clip(RoundedCornerShape(4.dp)))
                    Text(
                        uiState.greeting,
                        style         = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = Caveat,
                            fontSize   = 22.sp
                        ),
                        color         = MossGreen
                    )
                }
            }

            IconButton(
                onClick = onAccountClick,
                modifier = Modifier
                    .size(38.dp)
                    .background(CozyBlack.copy(alpha = 0.65f), CircleShape)
                    .border(1.dp, CozyBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = SoftTan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // ── Phase + NCI chip (top-right area below account) ─────────────────
        if (uiState.onboardingComplete && uiState.currentPhase != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 104.dp, end = 16.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onDailyInsightClick() },
                color    = CozyBlack.copy(alpha = 0.78f),
                shape    = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val phase = uiState.currentPhase!!
                    NciRingTiny(nci = uiState.nciScore)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Phase ${phase.number}",
                        style         = MaterialTheme.typography.labelSmall.copy(
                            fontFamily    = Caveat,
                            fontSize      = 15.sp,
                            letterSpacing = 0.sp
                        ),
                        color      = phase.color,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Day ${uiState.dayNumber}",
                        style         = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = Caveat,
                            fontSize   = 13.sp
                        ),
                        color = SoftTan
                    )
                }
            }
        }

        // ── 3D Mascot live viewport frame ──────────────────────────────────
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(260.dp)
                .tourTarget("home_mascot")
        )

        // ── Bottom cluster ───────────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp, start = 18.dp, end = 18.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Quick stats strip
            if (uiState.onboardingComplete) {
                QuickStatsStrip(uiState)
            }

            // 🚨 SURGE BRIDGE BUTTON
            Box(modifier = Modifier.tourTarget("home_surge_button")) {
                SurgeBridgeButton(onClick = onSurgeClick)
            }

            // Start Focus / Frog Break
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .tourTarget("home_focus_break_row"),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick  = onStartFocusSession,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = MossGreen,
                        contentColor   = CozyBlack
                    )
                ) {
                    Text(
                        "Deep Focus",
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick  = onDuckBreak,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape    = RoundedCornerShape(14.dp),
                    border   = BorderStroke(1.dp, WarmCream.copy(alpha = 0.18f)),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        containerColor = CozyBlack.copy(alpha = 0.5f),
                        contentColor   = WarmCream.copy(alpha = 0.85f)
                    )
                ) {
                    Text("Take a breather", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Surge Bridge Button — the hero action
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SurgeBridgeButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "surge_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.25f,
        targetValue   = 0.55f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label         = "glow_alpha"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.03f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label         = "surge_scale"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier.fillMaxWidth().scale(scale)
    ) {
        // Glow background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SurgeRed.copy(alpha = glowAlpha), Color.Transparent)
                    ),
                    RoundedCornerShape(18.dp)
                )
        )

        Button(
            onClick  = onClick,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape    = RoundedCornerShape(18.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = SurgeRed,
                contentColor   = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.yutorikaizen.R.drawable.ic_surge_alert), 
                    contentDescription = "Surge Alert", 
                    modifier = Modifier.size(24.dp).scale(1.3f).graphicsLayer { blendMode = BlendMode.Screen }.clip(RoundedCornerShape(4.dp))
                )
                Column {
                    Text(
                        "I feel the urge",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                    Text(
                        "Give me 90 seconds to reset",
                        fontSize = 10.sp,
                        color    = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Quick Stats Strip
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun QuickStatsStrip(uiState: HomeUiState) {
    val nciColor = when {
        uiState.nciScore > 0.7f -> NciHigh
        uiState.nciScore > 0.4f -> NciMid
        else                    -> NciLow
    }

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CozyBlack.copy(alpha = 0.72f))
            .border(1.dp, CozyBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        QuickStat(
            icon  = com.example.yutorikaizen.R.drawable.ic_streak_fire,
            value = "${uiState.currentStreak}",
            label = "streak"
        )
        Box(modifier = Modifier.width(1.dp).height(32.dp).background(CozyBorder.copy(alpha = 0.5f)))
        QuickStat(
            icon  = com.example.yutorikaizen.R.drawable.ic_habit_check,
            value = "${uiState.habitsCompletedToday}/${uiState.totalHabitsToday}",
            label = "habits"
        )
        Box(modifier = Modifier.width(1.dp).height(32.dp).background(CozyBorder.copy(alpha = 0.5f)))
        QuickStat(
            icon       = com.example.yutorikaizen.R.drawable.ic_brain_nci,
            value      = "${(uiState.nciScore * 100).toInt()}%",
            label      = "NCI",
            valueColor = nciColor
        )
    }
}

@Composable
private fun QuickStat(
    icon: Int,
    value: String,
    label: String,
    valueColor: Color = CreamWhite
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.padding(horizontal = 10.dp)
    ) {
        Image(
            painter           = androidx.compose.ui.res.painterResource(id = icon),
            contentDescription = null,
            modifier          = Modifier
                .size(18.dp)
                .scale(1.3f)
                .graphicsLayer { blendMode = BlendMode.Screen }
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.height(3.dp))
        Text(
            value,
            style      = MaterialTheme.typography.labelLarge,
            color      = valueColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            style    = MaterialTheme.typography.labelSmall,
            color    = SoftTan
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tiny NCI Ring for the overlay chip
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NciRingTiny(nci: Float) {
    val sweep by animateFloatAsState(
        targetValue   = nci * 360f,
        animationSpec = tween(1200),
        label         = "home_nci"
    )
    val color = when {
        nci > 0.7f -> NciHigh
        nci > 0.4f -> NciMid
        else       -> NciLow
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(44.dp)) {
        Canvas(modifier = Modifier.size(44.dp)) {
            drawArc(CozyBlack3, -90f, 360f, false, style = Stroke(5.dp.toPx(), cap = StrokeCap.Round))
            drawArc(color, -90f, sweep, false, style = Stroke(5.dp.toPx(), cap = StrokeCap.Round))
        }
        Text("${(nci * 100).toInt()}%", fontSize = 9.sp, color = color, fontWeight = FontWeight.Bold)
    }
}
