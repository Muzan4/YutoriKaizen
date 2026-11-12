package com.example.yutorikaizen.presentation.screens.journey

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.domain.model.NeuralPhase
import com.example.yutorikaizen.domain.model.PhaseHabit
import com.example.yutorikaizen.presentation.theme.*
import com.example.yutorikaizen.presentation.components.tourTarget
import androidx.compose.foundation.Canvas

@Composable
fun JourneyScreen(
    viewModel: JourneyViewModel = hiltViewModel(),
    onSurge: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    BackHandler(onBack = onBack)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showResetDialog by remember { mutableStateOf(false) }

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize().background(CozyBlack), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MossGreen)
        }
        return
    }

    val phase = uiState.currentPhase ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyBlack)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ── Top Navigation Bar ──────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .background(CozyBlack3.copy(alpha = 0.85f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = CreamWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "Journey",
                style = MaterialTheme.typography.titleMedium,
                color = SoftTan,
                fontWeight = FontWeight.Medium
            )
        }

        // ── Header ─────────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Column {
                Text(
                    "Day ${uiState.dayNumber}",
                    style      = MaterialTheme.typography.headlineLarge,
                    color      = CreamWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    uiState.program?.name ?: "",
                    style      = MaterialTheme.typography.labelMedium.copy(
                        fontFamily    = Caveat,
                        fontSize      = 18.sp,
                        letterSpacing = 0.sp
                    ),
                    color = MossGreen
                )
            }
            NciRingSmall(nci = uiState.nciScore)
        }

        Spacer(Modifier.height(24.dp))

        // ── Current Phase Card ──────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth().tourTarget("journey_phase_card")) {
            PhaseCard(
                phase           = phase,
                progressFraction = uiState.phaseProgressFraction,
                daysRemaining   = uiState.daysInPhaseRemaining
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── Today's Mission ─────────────────────────────────────────────────
        uiState.todayMission?.let { mission ->
            JourneySectionLabel("Today's Mission")
            Spacer(Modifier.height(8.dp))

            Card(
                shape  = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CozyBlack3),
                border = BorderStroke(1.dp, CozyAmber.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(mission.emoji, fontSize = 24.sp)
                        Column {
                            Text(mission.title, style = MaterialTheme.typography.labelLarge,
                                color = CreamWhite, fontWeight = FontWeight.Bold)
                            Text("Day ${uiState.dayNumber} challenge",
                                style = MaterialTheme.typography.labelSmall, color = CozyAmber)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(mission.description, style = MaterialTheme.typography.bodyMedium,
                        color = WarmCream.copy(alpha = 0.85f))
                    Spacer(Modifier.height(10.dp))
                    Surface(color = CozyBlack4, shape = RoundedCornerShape(10.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Action", style = MaterialTheme.typography.labelSmall, color = SoftTan)
                            Spacer(Modifier.height(4.dp))
                            Text(mission.actionPrompt, style = MaterialTheme.typography.bodySmall,
                                color = MossGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Surface(color = CozyBlack4, shape = RoundedCornerShape(10.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Reflect tonight", style = MaterialTheme.typography.labelSmall, color = SoftTan)
                            Spacer(Modifier.height(4.dp))
                            Text(mission.reflectionPrompt, style = MaterialTheme.typography.bodySmall,
                                color = WarmCream.copy(alpha = 0.8f), fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Today's Habits ──────────────────────────────────────────────────
        JourneySectionLabel("Today's Habits")
        Spacer(Modifier.height(10.dp))

        phase.habits.forEach { habit ->
            HabitRow(
                habit     = habit,
                isChecked = habit.id in uiState.completedHabitIds,
                onToggle  = { viewModel.toggleHabit(habit.id) }
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(20.dp))

        // ── Phase Timeline ──────────────────────────────────────────────────
        JourneySectionLabel("Your 4 Phases")
        Spacer(Modifier.height(10.dp))

        uiState.allPhases.forEach { p ->
            PhaseTimelineRow(
                phase     = p,
                isCurrent = p.number == phase.number,
                isUnlocked = p.number <= phase.number
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(20.dp))

        // ── Weekly Focus ────────────────────────────────────────────────────
        if (uiState.weeklyFocuses.isNotEmpty()) {
            val currentWeek = ((uiState.dayNumber - 1) / 7) + 1
            val weekFocus   = uiState.weeklyFocuses.getOrNull(currentWeek - 1)
                ?: uiState.weeklyFocuses.last()

            JourneySectionLabel("This Week's Focus")
            Spacer(Modifier.height(8.dp))
            Card(
                shape  = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Phase2Blue.copy(alpha = 0.08f)),
                border = BorderStroke(1.dp, Phase2Blue.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier          = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(52.dp)
                            .background(Phase2Blue.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Text(weekFocus.emoji, fontSize = 24.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Week $currentWeek",
                            style    = MaterialTheme.typography.labelSmall,
                            color    = Phase2Blue.copy(alpha = 0.8f),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            weekFocus.theme,
                            style      = MaterialTheme.typography.labelLarge,
                            color      = Phase2Blue,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(weekFocus.focus, style = MaterialTheme.typography.bodySmall,
                            color = WarmCream.copy(alpha = 0.8f), lineHeight = 18.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Reset Journey ───────────────────────────────────────────────────
        OutlinedButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SoftTan.copy(alpha = 0.25f)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = CozyBlack3, contentColor = SoftTan)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🔄", fontSize = 14.sp)
                Text("Reset Journey to Day 1", style = MaterialTheme.typography.labelMedium)
            }
        }

        Spacer(Modifier.height(80.dp))
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = CozyBlack2,
            title = {
                Text("Reset Journey to Day 1?", color = CozyAmber, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "This will reset your journey progress back to Day 1 (Phase 1: Reset & Detox) and clear previous journey check-ins.",
                    color = CreamWhite
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetJourney()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CozyAmber)
                ) {
                    Text("Reset to Day 1", color = CozyBlack)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = SoftTan)
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Journey Section Label
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun JourneySectionLabel(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = CreamWhite,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PhaseCard(phase: NeuralPhase, progressFraction: Float, daysRemaining: Int) {
    val sweepAnim by animateFloatAsState(
        targetValue   = progressFraction * 360f,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label         = "phase_sweep"
    )
    val phaseColor = phase.color

    Card(
        shape  = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = phaseColor.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.5.dp, phaseColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier          = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Small progress ring
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                Canvas(modifier = Modifier.size(72.dp)) {
                    drawArc(CozyBlack3, -90f, 360f, false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round))
                    drawArc(phaseColor, -90f, sweepAnim, false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round))
                }
                Text(phase.emoji, fontSize = 26.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Phase ${phase.number}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily    = Caveat,
                        fontSize      = 15.sp,
                        letterSpacing = 0.sp
                    ),
                    color = phaseColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(phase.name, style = MaterialTheme.typography.titleLarge,
                    color = CreamWhite, fontWeight = FontWeight.Bold)
                Text(phase.subtitle, style = MaterialTheme.typography.bodySmall, color = SoftTan)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (daysRemaining > 0) "$daysRemaining days remaining in this phase"
                    else "Phase complete!",
                    style = MaterialTheme.typography.labelSmall,
                    color = phaseColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Habit Row
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HabitRow(habit: PhaseHabit, isChecked: Boolean, onToggle: () -> Unit) {
    val bgColor by animateColorAsState(
        targetValue   = if (isChecked) MossGreenDim else CozyBlack3,
        animationSpec = tween(250),
        label         = "habit_bg"
    )
    val scale by animateFloatAsState(
        targetValue   = if (isChecked) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "habit_scale"
    )

    Card(
        onClick  = onToggle,
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = bgColor),
        border   = BorderStroke(1.dp, if (isChecked) MossGreen.copy(alpha = 0.4f) else CozyBorder),
        modifier = Modifier.fillMaxWidth()
            .scale(scale)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Checkbox visual
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .background(
                        if (isChecked) MossGreen else Color.Transparent,
                        CircleShape
                    )
                    .border(2.dp, if (isChecked) MossGreen else CozyBorder, CircleShape)
            ) {
                if (isChecked) Text("✓", color = CozyBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(habit.emoji, fontSize = 14.sp)
                    Text(
                        habit.title,
                        style      = MaterialTheme.typography.labelLarge,
                        color      = if (isChecked) MossGreen else CreamWhite,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    )
                    if (habit.isRequired) {
                        Surface(color = SurgeRed.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                            Text("required", style = MaterialTheme.typography.labelSmall,
                                color = SurgeRed, modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }
                Text(habit.description, style = MaterialTheme.typography.bodySmall,
                    color = SoftTan, maxLines = 2)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase Timeline Row
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PhaseTimelineRow(phase: NeuralPhase, isCurrent: Boolean, isUnlocked: Boolean) {
    val phaseColor = if (isUnlocked) phase.color else DimTan
    val bgColor    = if (isCurrent) phase.color.copy(alpha = 0.10f)
                     else if (isUnlocked) CozyBlack3
                     else CozyBlack2

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, if (isCurrent) phase.color.copy(alpha = 0.5f) else CozyBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier.size(36.dp)
                .background(phaseColor.copy(alpha = 0.15f), CircleShape)
        ) {
            Text(if (isUnlocked) phase.emoji else "🔒", fontSize = 16.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Phase ${phase.number}: ${phase.name}",
                style      = MaterialTheme.typography.labelLarge,
                color      = if (isCurrent) phase.color else if (isUnlocked) CreamWhite else DimTan,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal)
            Text("Day ${phase.startDay}–${if (phase.endDay < 900) phase.endDay.toString() else "∞"}",
                style = MaterialTheme.typography.labelSmall,
                color = if (isUnlocked) SoftTan else DimTan)
        }
        if (isCurrent) {
            Surface(color = phase.color.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                Text(" Active ", style = MaterialTheme.typography.labelSmall,
                    color = phase.color, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NCI mini ring
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NciRingSmall(nci: Float) {
    val sweep by animateFloatAsState(
        targetValue   = nci * 360f,
        animationSpec = tween(1200),
        label         = "nci_mini"
    )
    val color = when {
        nci > 0.7f -> NciHigh
        nci > 0.4f -> NciMid
        else       -> NciLow
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
        Canvas(modifier = Modifier.size(56.dp)) {
            drawArc(CozyBlack3, -90f, 360f, false, style = Stroke(6.dp.toPx(), cap = StrokeCap.Round))
            drawArc(color, -90f, sweep, false, style = Stroke(6.dp.toPx(), cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(nci * 100).toInt()}%", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
            Text("NCI", fontSize = 8.sp, color = SoftTan)
        }
    }
}
