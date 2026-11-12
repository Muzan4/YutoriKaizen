package com.example.yutorikaizen.presentation.screens.onboarding

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.R
import com.example.yutorikaizen.domain.model.DifficultyLevel
import com.example.yutorikaizen.domain.model.DopamineProgram
import com.example.yutorikaizen.domain.model.WeeklyFocus
import com.example.yutorikaizen.presentation.theme.*

private val TRIGGER_OPTIONS = listOf(
    "BOREDOM"  to "Boredom",
    "ANXIETY"  to "Anxiety",
    "LONELY"   to "Loneliness",
    "FATIGUE"  to "Exhaustion",
    "AVOIDING" to "Avoidance"
)

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val totalSteps = 4

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Progress bar + step label ────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp, bottom = 0.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val stepLabels = listOf("Welcome", "Your Program", "Your Pace", "Your Triggers")
                    Text(
                        stepLabels.getOrElse(uiState.step) { "" },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = Caveat,
                            fontSize = 16.sp,
                            letterSpacing = 0.sp
                        ),
                        color = MossGreen
                    )
                    Text(
                        "${uiState.step + 1} / $totalSteps",
                        style = MaterialTheme.typography.labelSmall,
                        color = DimTan
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Segmented progress bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    repeat(totalSteps) { index ->
                        val fraction by animateFloatAsState(
                            targetValue = when {
                                index < uiState.step  -> 1f
                                index == uiState.step -> 1f
                                else                  -> 0f
                            },
                            animationSpec = tween(350, easing = EaseOutCubic),
                            label = "seg_$index"
                        )
                        val color by animateColorAsState(
                            targetValue = when {
                                index <= uiState.step -> MossGreen
                                else                  -> CozyBlack3
                            },
                            animationSpec = tween(350),
                            label = "seg_color_$index"
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(CozyBlack3)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Step content ─────────────────────────────────────────────────
            AnimatedContent(
                targetState   = uiState.step,
                transitionSpec = {
                    if (targetState > initialState)
                        (slideInHorizontally { it / 2 } + fadeIn(tween(300))) togetherWith
                                (slideOutHorizontally { -it / 2 } + fadeOut(tween(200)))
                    else
                        (slideInHorizontally { -it / 2 } + fadeIn(tween(300))) togetherWith
                                (slideOutHorizontally { it / 2 } + fadeOut(tween(200)))
                },
                label    = "onboarding_content",
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    0 -> WelcomeStep()
                    1 -> ProgramStep(
                        programs = viewModel.programs,
                        selected = uiState.selectedProgram,
                        onSelect = viewModel::selectProgram
                    )
                    2 -> DifficultyStep(
                        selected    = uiState.selectedDifficulty,
                        recommended = uiState.selectedProgram?.recommendedDifficulty,
                        onSelect    = viewModel::selectDifficulty
                    )
                    3 -> TriggersStep(
                        selectedTriggers = uiState.selectedTriggers,
                        onToggle         = viewModel::toggleTrigger
                    )
                }
            }

            // ── Bottom action area ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val canProceed = when (uiState.step) {
                    1    -> uiState.selectedProgram != null
                    2    -> uiState.selectedDifficulty != null
                    3    -> uiState.selectedTriggers.isNotEmpty()
                    else -> true
                }

                val isLast = uiState.step == 3

                Button(
                    onClick  = {
                        if (!isLast) viewModel.nextStep()
                        else viewModel.completeOnboarding(onFinished)
                    },
                    enabled  = canProceed && !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor         = MossGreen,
                        contentColor           = CozyBlack,
                        disabledContainerColor = CozyBlack3,
                        disabledContentColor   = DimTan
                    )
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(20.dp),
                            color       = CozyBlack,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = when (uiState.step) {
                                0    -> "Let's begin"
                                3    -> "Start my journey"
                                else -> "Continue"
                            },
                            fontWeight    = FontWeight.SemiBold,
                            fontSize      = 16.sp,
                            letterSpacing = 0.sp
                        )
                    }
                }

                if (uiState.step > 0) {
                    TextButton(
                        onClick  = viewModel::prevStep,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Back",
                            color    = DimTan,
                            fontSize = 14.sp,
                            style    = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 0 — Welcome
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun WelcomeStep() {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.15f,
        targetValue   = 0.30f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // Hero orb
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier.size(140.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            listOf(MossGreen.copy(alpha = glowAlpha), Color.Transparent)
                        ),
                        CircleShape
                    )
            )
            Image(
                painter = painterResource(id = R.drawable.ic_ribi_head),
                contentDescription = null,
                modifier = Modifier.size(76.dp).clip(CircleShape)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Your brain\ngot hacked.",
            style     = MaterialTheme.typography.displayMedium.copy(
                fontFamily = Fraunces,
                fontSize   = 38.sp,
                lineHeight = 44.sp
            ),
            color     = CreamWhite,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Every app fighting for your attention was designed by hundreds of engineers whose only job was making sure you can't stop. This isn't a willpower problem.",
            style      = MaterialTheme.typography.bodyLarge,
            color      = SoftTan,
            textAlign  = TextAlign.Center,
            lineHeight = 26.sp
        )

        Spacer(Modifier.height(6.dp))

        Text(
            "It's an engineering problem.",
            style      = MaterialTheme.typography.bodyLarge,
            color      = MossGreen,
            textAlign  = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 26.sp
        )

        Spacer(Modifier.height(32.dp))

        // 2x2 feature grid
        val features = listOf(
            Triple(Icons.Rounded.Bolt, "Surge Bridge", "90-second urge reset"),
            Triple(Icons.Rounded.HourglassEmpty, "Deep Focus", "25-min attention blocks"),
            Triple(Icons.Rounded.Air, "Rest Break", "Screen-free mental reset"),
            Triple(Icons.Rounded.QueryStats, "Daily Insight", "Consistency score index")
        )
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            features.chunked(2).forEach { row ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    row.forEach { (icon, title, subtitle) ->
                        FeatureCell(
                            icon     = icon,
                            title    = title,
                            subtitle = subtitle,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun FeatureCell(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color    = CozyBlack3,
        shape    = RoundedCornerShape(16.dp),
        border   = BorderStroke(1.dp, CozyBorder)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MossGreen, modifier = Modifier.size(24.dp))
            Text(
                title,
                style      = MaterialTheme.typography.labelLarge,
                color      = CreamWhite,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                subtitle,
                style    = MaterialTheme.typography.labelSmall,
                color    = SoftTan,
                lineHeight = 16.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 1 — Program
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProgramStep(
    programs: List<DopamineProgram>,
    selected: DopamineProgram?,
    onSelect: (DopamineProgram) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                "What do you\nwant to change?",
                style      = MaterialTheme.typography.headlineLarge,
                color      = CreamWhite,
                lineHeight = 36.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Pick the one that sounds most like you right now.",
                style    = MaterialTheme.typography.bodyMedium,
                color    = SoftTan,
                lineHeight = 22.sp
            )
        }

        Spacer(Modifier.height(18.dp))

        Column(
            modifier            = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            programs.forEach { program ->
                ProgramCard(
                    program    = program,
                    isSelected = program == selected,
                    onClick    = { onSelect(program) }
                )
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun ProgramCard(
    program: DopamineProgram,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue   = if (isSelected) MossGreen else CozyBorder,
        animationSpec = tween(200),
        label         = "prog_border"
    )
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) MossGreenDim else CozyBlack2,
        animationSpec = tween(200),
        label         = "prog_bg"
    )

    Card(
        onClick  = onClick,
        shape    = RoundedCornerShape(18.dp),
        border   = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
        colors   = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header row: emoji + name + duration badge
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (isSelected) MossGreen.copy(alpha = 0.18f) else CozyBlack3,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Text(program.emoji, fontSize = 20.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        program.name,
                        style      = MaterialTheme.typography.labelLarge,
                        color      = if (isSelected) MossGreen else CreamWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        program.tagline,
                        style    = MaterialTheme.typography.labelSmall,
                        color    = SoftTan,
                        lineHeight = 16.sp
                    )
                }

                // Duration + difficulty badge
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = program.recommendedDifficulty.badgeColor().copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "${program.durationDays}d",
                            style    = MaterialTheme.typography.labelSmall,
                            color    = program.recommendedDifficulty.badgeColor(),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = CozyBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            // Description
            Text(
                program.description,
                style    = MaterialTheme.typography.bodySmall,
                color    = if (isSelected) WarmCream.copy(alpha = 0.9f) else SoftTan,
                lineHeight = 18.sp
            )

            // Weekly focus theme pills (first 3)
            if (program.weeklyFocus.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    program.weeklyFocus.take(3).forEach { focus ->
                        WeekFocusPill(focus = focus, isSelected = isSelected)
                    }
                    if (program.weeklyFocus.size > 3) {
                        Surface(
                            color = CozyBlack3,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "+${program.weeklyFocus.size - 3}",
                                style    = MaterialTheme.typography.labelSmall,
                                color    = DimTan,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekFocusPill(focus: WeeklyFocus, isSelected: Boolean) {
    Surface(
        color = if (isSelected) MossGreen.copy(alpha = 0.15f) else CozyBlack3,
        shape = RoundedCornerShape(6.dp),
        border = if (isSelected) BorderStroke(0.5.dp, MossGreen.copy(alpha = 0.4f)) else null
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(focus.emoji, fontSize = 10.sp)
            Text(
                focus.theme,
                style    = MaterialTheme.typography.labelSmall,
                color    = if (isSelected) MossGreen else SoftTan,
                fontSize = 10.sp
            )
        }
    }
}

private fun DifficultyLevel.badgeColor() = when (this) {
    DifficultyLevel.GENTLE     -> MossGreen
    DifficultyLevel.CHALLENGER -> CozyAmber
    DifficultyLevel.HARD       -> SurgeRed
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 2 — Difficulty
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DifficultyStep(
    selected: DifficultyLevel?,
    recommended: DifficultyLevel?,
    onSelect: (DifficultyLevel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            "Be honest\nwith yourself.",
            style      = MaterialTheme.typography.headlineLarge,
            color      = CreamWhite,
            lineHeight = 36.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Starting too hard is how people quit on day 3. Pick what you'll actually stick with.",
            style    = MaterialTheme.typography.bodyMedium,
            color    = SoftTan,
            lineHeight = 22.sp
        )
        Spacer(Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            DifficultyLevel.entries.forEach { level ->
                DifficultyCard(
                    level         = level,
                    isSelected    = level == selected,
                    isRecommended = level == recommended,
                    onClick       = { onSelect(level) }
                )
            }
        }
    }
}

@Composable
private fun DifficultyCard(
    level: DifficultyLevel,
    isSelected: Boolean,
    isRecommended: Boolean,
    onClick: () -> Unit
) {
    val accentColor = level.badgeColor()
    val borderColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor else CozyBorder,
        animationSpec = tween(200),
        label         = "diff_border"
    )
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor.copy(alpha = 0.08f) else CozyBlack2,
        animationSpec = tween(200),
        label         = "diff_bg"
    )

    val humanDesc = when (level) {
        DifficultyLevel.GENTLE     -> "Slow and steady. Build the habit without burning out."
        DifficultyLevel.CHALLENGER -> "Real friction, real growth. You'll feel the resistance."
        DifficultyLevel.HARD       -> "All in. Full protocol. No compromises."
    }
    val subtext = when (level) {
        DifficultyLevel.GENTLE     -> "Best for starting out"
        DifficultyLevel.CHALLENGER -> "Most people choose this"
        DifficultyLevel.HARD       -> "For when you're done messing around"
    }

    Card(
        onClick  = onClick,
        shape    = RoundedCornerShape(18.dp),
        border   = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
        colors   = CardDefaults.cardColors(containerColor = bgColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(accentColor.copy(alpha = if (isSelected) 0.2f else 0.1f), RoundedCornerShape(12.dp))
            ) {
                Text(level.emoji, fontSize = 24.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        level.label,
                        style      = MaterialTheme.typography.labelLarge,
                        color      = if (isSelected) accentColor else CreamWhite,
                        fontWeight = FontWeight.Bold
                    )
                    if (isRecommended) {
                        Surface(
                            color = accentColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Text(
                                "recommended",
                                style    = MaterialTheme.typography.labelSmall,
                                color    = accentColor,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    humanDesc,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = if (isSelected) WarmCream.copy(alpha = 0.85f) else SoftTan,
                    lineHeight = 17.sp
                )
                Text(
                    subtext,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = if (isSelected) accentColor.copy(alpha = 0.8f) else DimTan,
                    fontSize = 10.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 3 — Triggers
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TriggersStep(
    selectedTriggers: Set<String>,
    onToggle: (String) -> Unit
) {
    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "What makes\nyou reach for it?",
            style      = MaterialTheme.typography.headlineLarge,
            color      = CreamWhite,
            lineHeight = 36.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Not judgment — just useful data. Select all that apply.",
            style    = MaterialTheme.typography.bodyMedium,
            color    = SoftTan,
            lineHeight = 22.sp
        )
        Spacer(Modifier.height(24.dp))

        val triggerDescriptions = mapOf(
            "BOREDOM"  to "Nothing interesting is happening",
            "ANXIETY"  to "Something is making me uncomfortable",
            "LONELY"   to "I want some kind of connection",
            "FATIGUE"  to "I'm tired and I want to zone out",
            "AVOIDING" to "There's something I don't want to do"
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TRIGGER_OPTIONS.forEach { (key, label) ->
                val isSelected = key in selectedTriggers
                val bgColor by animateColorAsState(
                    targetValue   = if (isSelected) MossGreenDim else CozyBlack2,
                    animationSpec = tween(200),
                    label         = "trigger_bg_$key"
                )
                val borderColor by animateColorAsState(
                    targetValue   = if (isSelected) MossGreen else CozyBorder,
                    animationSpec = tween(200),
                    label         = "trigger_border_$key"
                )

                Card(
                    onClick  = { onToggle(key) },
                    shape    = RoundedCornerShape(16.dp),
                    border   = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
                    colors   = CardDefaults.cardColors(containerColor = bgColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier              = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Checkbox
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(22.dp)
                                .background(
                                    if (isSelected) MossGreen else Color.Transparent,
                                    CircleShape
                                )
                                .border(1.5.dp, if (isSelected) MossGreen else CozyBorder.copy(alpha = 0.6f), CircleShape)
                        ) {
                            if (isSelected) {
                                Text("✓", color = CozyBlack, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                label,
                                style      = MaterialTheme.typography.labelLarge,
                                color      = if (isSelected) MossGreen else CreamWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                triggerDescriptions[key] ?: "",
                                style    = MaterialTheme.typography.labelSmall,
                                color    = if (isSelected) MossGreen.copy(alpha = 0.7f) else DimTan,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
