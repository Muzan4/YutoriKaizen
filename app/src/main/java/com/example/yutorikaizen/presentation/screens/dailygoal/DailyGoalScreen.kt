package com.example.yutorikaizen.presentation.screens.dailygoal

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.Canvas
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.domain.model.DailyMission
import com.example.yutorikaizen.domain.model.NeuralPhase
import com.example.yutorikaizen.presentation.theme.*
import com.example.yutorikaizen.presentation.components.tourTarget

@Composable
fun DailyGoalScreen(
    onBack: () -> Unit = {},
    viewModel: DailyGoalViewModel = hiltViewModel()
) {
    BackHandler(onBack = onBack)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(
            Modifier.fillMaxSize().background(CozyBlack),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MossGreen)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyBlack)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // ─── Hero Header ───────────────────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth().tourTarget("daily_goal_card")) {
            DailyGoalHeader(
                dayNumber = uiState.dayNumber,
                dateLabel = uiState.dateLabel,
                nciScore = uiState.nciScore,
                phaseHabitsCompleted = uiState.phaseHabitsCompleted,
                phaseHabitsTotal = uiState.phaseHabitsTotal,
                onBack = onBack
            )
        }

        Spacer(Modifier.height(20.dp))

        // ─── Identity & Morning Intention ─────────────────────────────────
        SectionHeader(
            title = "Morning Anchor",
            subtitle = "Your single non-negotiable intention for today"
        )
        Spacer(Modifier.height(10.dp))
        IntentionCard(
            text = uiState.morningIntention,
            identity = uiState.identityStatement,
            isSaved = uiState.intentionSaved,
            placeholder = "e.g. Put my phone face-down during deep work and stay present in every conversation",
            accentColor = MossGreen,
            onEdit = { viewModel.showIntentionEdit() }
        )

        Spacer(Modifier.height(24.dp))

        // ─── Implementation Intention (Anti-Trigger Contract) ──────────────
        SectionHeader(
            title = "Habit Reset Contract",
            subtitle = "If-Then plan: re-routing urges to healthy resets"
        )
        Spacer(Modifier.height(10.dp))
        AntiTriggerCard(
            urge = uiState.antiTriggerUrge,
            action = uiState.antiTriggerAction,
            isSaved = uiState.antiTriggerSaved,
            onEdit = { viewModel.showAntiTriggerEdit() }
        )

        Spacer(Modifier.height(24.dp))

        // ─── Mood & Energy Check-in ────────────────────────────────────────
        SectionHeader(
            title = "Internal Energy State",
            subtitle = "Notice your biological baseline without judgment"
        )
        Spacer(Modifier.height(10.dp))
        MoodEnergyCard(
            moodRating = uiState.moodRating,
            energyRating = uiState.energyRating,
            onMoodChange = { viewModel.setMood(it) },
            onEnergyChange = { viewModel.setEnergy(it) }
        )

        Spacer(Modifier.height(24.dp))

        // ─── Today's Neuro-Mission ─────────────────────────────────────────
        uiState.todayMission?.let { mission ->
            SectionHeader(
                title = "Today's Rewiring Mission",
                subtitle = "Day ${uiState.dayNumber} neuro-adaptation challenge"
            )
            Spacer(Modifier.height(10.dp))
            MissionCard(mission = mission, dayNumber = uiState.dayNumber)
            Spacer(Modifier.height(24.dp))
        }

        // ─── Phase Progress ────────────────────────────────────────────────
        uiState.currentPhase?.let { phase ->
            SectionHeader(
                title = "Phase ${phase.number}: ${phase.name}",
                subtitle = phase.subtitle
            )
            Spacer(Modifier.height(10.dp))
            PhaseProgressCard(
                phase = phase,
                completed = uiState.phaseHabitsCompleted,
                total = uiState.phaseHabitsTotal
            )
            Spacer(Modifier.height(24.dp))
        }

        // ─── Evening Reflection & Win ──────────────────────────────────────
        SectionHeader(
            title = "Evening Win & Reflection",
            subtitle = "Celebrate the win & lock in today's growth"
        )
        Spacer(Modifier.height(10.dp))
        EveningReflectionCard(
            win = uiState.winOfTheDay,
            reflection = uiState.eveningReflection,
            isSaved = uiState.reflectionSaved,
            onEdit = { viewModel.showReflectionEdit() }
        )

        Spacer(Modifier.height(24.dp))

        // ─── Reset Tomorrow ────────────────────────────────────────────────
        SectionHeader(
            title = "Reset Your Tomorrow",
            subtitle = "Evening habits that set up your future self for victory"
        )
        Spacer(Modifier.height(10.dp))
        ResetTomorrowCard(
            prepPhone = uiState.prepPhone,
            onPrepPhone = { viewModel.togglePrepPhone(it) },
            prepScreens = uiState.prepScreens,
            onPrepScreens = { viewModel.togglePrepScreens(it) },
            prepStillness = uiState.prepStillness,
            onPrepStillness = { viewModel.togglePrepStillness(it) }
        )

        Spacer(Modifier.height(24.dp))

        // ─── Daily Quote ───────────────────────────────────────────────────
        DailyQuoteCard(dayNumber = uiState.dayNumber)
    }

    // ─── Edit Dialogs ──────────────────────────────────────────────────────
    if (uiState.showIntentionEdit) {
        IntentionEditDialog(
            initialIntention = uiState.morningIntention,
            initialIdentity = uiState.identityStatement,
            onDismiss = { viewModel.hideIntentionEdit() },
            onSave = { viewModel.saveIntention(it) }
        )
    }

    if (uiState.showAntiTriggerEdit) {
        AntiTriggerDialog(
            initialUrge = uiState.antiTriggerUrge,
            initialAction = uiState.antiTriggerAction,
            initialIdentity = uiState.identityStatement,
            onDismiss = { viewModel.hideAntiTriggerEdit() },
            onSave = { urge, action, identity ->
                viewModel.saveAntiTrigger(urge, action, identity)
            }
        )
    }

    if (uiState.showReflectionEdit) {
        ReflectionEditDialog(
            initialWin = uiState.winOfTheDay,
            initialReflection = uiState.eveningReflection,
            onDismiss = { viewModel.hideReflectionEdit() },
            onSave = { text, win -> viewModel.saveReflection(text, win) }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DailyGoalHeader(
    dayNumber: Int,
    dateLabel: String,
    nciScore: Float,
    phaseHabitsCompleted: Int,
    phaseHabitsTotal: Int,
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors  = listOf(CozyBlack2, CozyBlack),
                    startY  = 0f,
                    endY    = Float.POSITIVE_INFINITY
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 14.dp)
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
                "Daily Goal",
                style = MaterialTheme.typography.titleMedium,
                color = SoftTan,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Day $dayNumber",
                    style      = MaterialTheme.typography.headlineLarge,
                    color      = CreamWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    dateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftTan
                )
                Spacer(Modifier.height(10.dp))
                if (phaseHabitsTotal > 0) {
                    val progress = phaseHabitsCompleted.toFloat() / phaseHabitsTotal
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(0.65f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "$phaseHabitsCompleted/$phaseHabitsTotal habits",
                                style = MaterialTheme.typography.labelSmall,
                                color = MossGreen
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(CozyBlack3)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MossGreen)
                            )
                        }
                    }
                }
            }

            NciRing(nciScore)
        }
    }
}

@Composable
private fun NciRing(nci: Float) {
    val sweep by animateFloatAsState(
        targetValue = nci * 360f,
        animationSpec = tween(1200),
        label = "nci"
    )
    val color = when {
        nci > 0.7f -> NciHigh
        nci > 0.4f -> NciMid
        else -> NciLow
    }
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
        Canvas(modifier = Modifier.size(64.dp)) {
            drawArc(CozyBlack3, -90f, 360f, false, style = Stroke(6.dp.toPx(), cap = StrokeCap.Round))
            if (nci > 0f) drawArc(color, -90f, sweep, false, style = Stroke(6.dp.toPx(), cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${(nci * 100).toInt()}%", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
            Text("NCI", fontSize = 8.sp, color = SoftTan)
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = CreamWhite,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(2.dp))
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = SoftTan)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Intention Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun IntentionCard(
    text: String,
    identity: String,
    isSaved: Boolean,
    placeholder: String,
    accentColor: Color,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onEdit() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSaved) accentColor.copy(alpha = 0.08f) else CozyBlack2
        ),
        border = BorderStroke(
            1.dp,
            if (isSaved) accentColor.copy(alpha = 0.4f) else CozyBorder
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        if (isSaved) Icons.Rounded.CheckCircle else Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = if (isSaved) accentColor else SoftTan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (isSaved) "Today's Commitment" else "Tap to set commitment",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSaved) accentColor else SoftTan,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (identity.isNotBlank()) {
                        Text(
                            "Identity: \"$identity\"",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftTan,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = if (isSaved && text.isNotBlank()) text else placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSaved && text.isNotBlank()) CreamWhite else DimTan,
                fontStyle = if (isSaved && text.isNotBlank()) FontStyle.Normal else FontStyle.Italic
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Anti-Trigger Contract Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AntiTriggerCard(
    urge: String,
    action: String,
    isSaved: Boolean,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onEdit() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSaved) Phase2Blue.copy(alpha = 0.08f) else CozyBlack2
        ),
        border = BorderStroke(
            1.dp,
            if (isSaved) Phase2Blue.copy(alpha = 0.4f) else CozyBorder
        )
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Phase2Blue.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        if (isSaved) Icons.Rounded.CheckCircle else Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = if (isSaved) Phase2Blue else SoftTan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (isSaved) "Urge Redirect Active" else "Set If-Then Redirect Plan",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSaved) Phase2Blue else SoftTan,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Rewiring the brain's automatic dopamine loop",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftTan
                    )
                }
            }

            if (isSaved && urge.isNotBlank() && action.isNotBlank()) {
                Surface(
                    color = CozyBlack3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("WHEN: ", style = MaterialTheme.typography.labelSmall, color = SurgeRed, fontWeight = FontWeight.Bold)
                            Text(urge, style = MaterialTheme.typography.bodySmall, color = CreamWhite)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("THEN: ", style = MaterialTheme.typography.labelSmall, color = MossGreen, fontWeight = FontWeight.Bold)
                            Text(action, style = MaterialTheme.typography.bodySmall, color = CreamWhite, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            } else {
                Text(
                    "\"When I feel bored/anxious and want to open social media, I will take 3 deep breaths and drink water instead.\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = DimTan,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Mood & Energy Rating Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MoodEnergyCard(
    moodRating: Int,
    energyRating: Int,
    onMoodChange: (Int) -> Unit,
    onEnergyChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack2),
        border = BorderStroke(1.dp, CozyBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            RatingRow(
                label = "Mood",
                rating = moodRating,
                onRate = onMoodChange
            )
            HorizontalDivider(color = CozyBorder, thickness = 0.5.dp)
            RatingRow(
                label = "Energy",
                rating = energyRating,
                onRate = onEnergyChange
            )
        }
    }
}

@Composable
private fun RatingRow(
    label: String,
    rating: Int,
    onRate: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = SoftTan,
            modifier = Modifier.width(60.dp)
        )
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..5).forEach { level ->
                val isSelected = rating == level
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (isSelected) MossGreenDim else CozyBlack3,
                            CircleShape
                        )
                        .border(1.dp, if (isSelected) MossGreen else CozyBorder, CircleShape)
                        .clickable { onRate(level) }
                ) {
                    Text(
                        "$level",
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MossGreen else SoftTan
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Mission Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MissionCard(mission: DailyMission, dayNumber: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack3),
        border = BorderStroke(1.dp, CozyAmber.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(mission.emoji, fontSize = 24.sp)
                Column {
                    Text(
                        mission.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = CreamWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Day $dayNumber neuro-challenge",
                        style = MaterialTheme.typography.labelSmall,
                        color = CozyAmber
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                mission.description,
                style = MaterialTheme.typography.bodyMedium,
                color = WarmCream.copy(alpha = 0.85f)
            )

            Spacer(Modifier.height(10.dp))
            Surface(color = CozyBlack4, shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Action to take",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftTan
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        mission.actionPrompt,
                        style = MaterialTheme.typography.bodySmall,
                        color = MossGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Surface(color = CozyBlack4, shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Reflect tonight",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftTan
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        mission.reflectionPrompt,
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmCream.copy(alpha = 0.8f),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Phase Progress Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PhaseProgressCard(phase: NeuralPhase, completed: Int, total: Int) {
    val progress = if (total > 0) completed.toFloat() / total else 0f
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "phase_progress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = phase.color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, phase.color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        phase.name,
                        style = MaterialTheme.typography.labelLarge,
                        color = phase.color,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "$completed of $total habits checked today",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftTan
                    )
                }
                Text("${(animProgress * 100).toInt()}%", color = phase.color, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(CozyBlack3)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animProgress)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(phase.color)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Evening Reflection & Win Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun EveningReflectionCard(
    win: String,
    reflection: String,
    isSaved: Boolean,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onEdit() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSaved) Phase4Purple.copy(alpha = 0.08f) else CozyBlack2
        ),
        border = BorderStroke(
            1.dp,
            if (isSaved) Phase4Purple.copy(alpha = 0.4f) else CozyBorder
        )
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Phase4Purple.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        if (isSaved) Icons.Rounded.CheckCircle else Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = if (isSaved) Phase4Purple else SoftTan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (isSaved) "Reflection & Win Recorded" else "Record Today's Reflection",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSaved) Phase4Purple else SoftTan,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Rewiring reward circuits by noticing small victories",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftTan
                    )
                }
            }

            if (isSaved && (win.isNotBlank() || reflection.isNotBlank())) {
                Surface(
                    color = CozyBlack3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (win.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("WIN: ", style = MaterialTheme.typography.labelSmall, color = CozyAmber, fontWeight = FontWeight.Bold)
                                Text(win, style = MaterialTheme.typography.bodySmall, color = CreamWhite)
                            }
                        }
                        if (reflection.isNotBlank()) {
                            Text(reflection, style = MaterialTheme.typography.bodySmall, color = WarmCream.copy(alpha = 0.85f), fontStyle = FontStyle.Italic)
                        }
                    }
                }
            } else {
                Text(
                    "Tap to capture what went well and what you learned about yourself today...",
                    style = MaterialTheme.typography.bodySmall,
                    color = DimTan,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reset Tomorrow Pre-Commitment Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ResetTomorrowCard(
    prepPhone: Boolean,
    onPrepPhone: (Boolean) -> Unit,
    prepScreens: Boolean,
    onPrepScreens: (Boolean) -> Unit,
    prepStillness: Boolean,
    onPrepStillness: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack2),
        border = BorderStroke(1.dp, CozyBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ResetCheckItem(
                label = "Charge phone outside bedroom tonight",
                checked = prepPhone,
                onToggle = onPrepPhone
            )
            ResetCheckItem(
                label = "Screens off 1 hour before sleep",
                checked = prepScreens,
                onToggle = onPrepScreens
            )
            ResetCheckItem(
                label = "5 minutes of unstructured quiet or stretch",
                checked = prepStillness,
                onToggle = onPrepStillness
            )
        }
    }
}

@Composable
private fun ResetCheckItem(
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        onClick = { onToggle(!checked) },
        shape = RoundedCornerShape(12.dp),
        color = if (checked) MossGreenDim else CozyBlack3,
        border = BorderStroke(1.dp, if (checked) MossGreen.copy(alpha = 0.4f) else CozyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(24.dp)
                    .background(if (checked) MossGreen else Color.Transparent, CircleShape)
                    .border(1.5.dp, if (checked) MossGreen else CozyBorder, CircleShape)
            ) {
                if (checked) {
                    Text("✓", color = CozyBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (checked) MossGreen else CreamWhite,
                fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Daily Quote Card
// ─────────────────────────────────────────────────────────────────────────────
private val DAILY_QUOTES = listOf(
    "Every action you take is a vote for the person you wish to become." to "James Clear",
    "You do not rise to the level of your goals. You fall to the level of your systems." to "James Clear",
    "Small disciplines repeated with consistency every day lead to great achievements." to "John C. Maxwell",
    "The secret of change is to focus all of your energy not on fighting the old, but on building the new." to "Socrates",
    "It does not matter how slowly you go as long as you do not stop." to "Confucius",
    "Discipline is choosing between what you want now and what you want most." to "Abraham Lincoln",
    "We are what we repeatedly do. Excellence, then, is not an act, but a habit." to "Aristotle",
    "Between stimulus and response there is a space. In that space is our power to choose our response." to "Viktor Frankl",
    "Don't judge each day by the harvest you reap, but by the seeds that you plant." to "Robert Louis Stevenson",
    "Consistency is more important than perfection." to "Zen Proverb"
)

@Composable
private fun DailyQuoteCard(dayNumber: Int) {
    val (quote, author) = DAILY_QUOTES[(dayNumber - 1) % DAILY_QUOTES.size]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack2),
        border = BorderStroke(1.dp, CozyBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("“", fontSize = 32.sp, color = MossGreen.copy(alpha = 0.4f), fontWeight = FontWeight.Bold)
            Text(
                quote,
                style = MaterialTheme.typography.bodyMedium,
                color = WarmCream,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic
            )
            Text(
                "— $author",
                style = MaterialTheme.typography.labelSmall,
                color = SoftTan
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Dialogs
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun IntentionEditDialog(
    initialIntention: String,
    initialIdentity: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialIntention) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CozyBlack2)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Set Your Daily Anchor",
                    style = MaterialTheme.typography.titleMedium,
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "What is the single most important habit or intention you commit to today, no matter what?",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftTan
                )

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("e.g. No social media before 12 PM, and 20 mins of reading tonight", color = DimTan, fontStyle = FontStyle.Italic) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MossGreen,
                        unfocusedBorderColor = CozyBorder,
                        focusedTextColor = CreamWhite,
                        unfocusedTextColor = CreamWhite,
                        cursorColor = MossGreen
                    ),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 5
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = SoftTan)
                    }
                    Button(
                        onClick = { onSave(text.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = MossGreen, contentColor = CozyBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lock It In", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AntiTriggerDialog(
    initialUrge: String,
    initialAction: String,
    initialIdentity: String,
    onDismiss: () -> Unit,
    onSave: (urge: String, action: String, identity: String) -> Unit
) {
    var urge by remember { mutableStateOf(initialUrge) }
    var action by remember { mutableStateOf(initialAction) }
    var identity by remember { mutableStateOf(initialIdentity) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CozyBlack2)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "If-Then Reset Contract",
                    style = MaterialTheme.typography.titleMedium,
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Implementation intentions rewire your brain by pre-deciding your reaction before the urge strikes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftTan
                )

                OutlinedTextField(
                    value = urge,
                    onValueChange = { urge = it },
                    label = { Text("When I feel the urge to...", color = SoftTan) },
                    placeholder = { Text("e.g. check Instagram while working", color = DimTan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SurgeRed,
                        unfocusedBorderColor = CozyBorder,
                        focusedTextColor = CreamWhite,
                        unfocusedTextColor = CreamWhite,
                        cursorColor = SurgeRed
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = action,
                    onValueChange = { action = it },
                    label = { Text("I will instead...", color = SoftTan) },
                    placeholder = { Text("e.g. take 3 deep sighs and drink water", color = DimTan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MossGreen,
                        unfocusedBorderColor = CozyBorder,
                        focusedTextColor = CreamWhite,
                        unfocusedTextColor = CreamWhite,
                        cursorColor = MossGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = identity,
                    onValueChange = { identity = it },
                    label = { Text("Because I am becoming someone who...", color = SoftTan) },
                    placeholder = { Text("e.g. values deep focus over quick hits", color = DimTan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Phase2Blue,
                        unfocusedBorderColor = CozyBorder,
                        focusedTextColor = CreamWhite,
                        unfocusedTextColor = CreamWhite,
                        cursorColor = Phase2Blue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = SoftTan)
                    }
                    Button(
                        onClick = { onSave(urge.trim(), action.trim(), identity.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Phase2Blue, contentColor = CozyBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Contract", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReflectionEditDialog(
    initialWin: String,
    initialReflection: String,
    onDismiss: () -> Unit,
    onSave: (reflection: String, win: String) -> Unit
) {
    var win by remember { mutableStateOf(initialWin) }
    var reflection by remember { mutableStateOf(initialReflection) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CozyBlack2)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "Evening Reflection & Win",
                    style = MaterialTheme.typography.titleMedium,
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = win,
                    onValueChange = { win = it },
                    label = { Text("Today's Win", color = SoftTan) },
                    placeholder = { Text("One thing you resisted or accomplished...", color = DimTan) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CozyAmber,
                        unfocusedBorderColor = CozyBorder,
                        focusedTextColor = CreamWhite,
                        unfocusedTextColor = CreamWhite,
                        cursorColor = CozyAmber
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = reflection,
                    onValueChange = { reflection = it },
                    label = { Text("Reflection", color = SoftTan) },
                    placeholder = { Text("What did you learn today? What will you do better tomorrow?", color = DimTan) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Phase4Purple,
                        unfocusedBorderColor = CozyBorder,
                        focusedTextColor = CreamWhite,
                        unfocusedTextColor = CreamWhite,
                        cursorColor = Phase4Purple
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = SoftTan)
                    }
                    Button(
                        onClick = { onSave(reflection.trim(), win.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = Phase4Purple, contentColor = CreamWhite),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Reflection", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
