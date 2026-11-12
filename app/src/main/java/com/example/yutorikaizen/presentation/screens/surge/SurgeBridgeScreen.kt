package com.example.yutorikaizen.presentation.screens.surge

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check

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
import com.example.yutorikaizen.presentation.theme.*
import androidx.compose.foundation.Canvas

private val SURGE_TOTAL = 90f

@Composable
fun SurgeBridgeScreen(
    viewModel: SurgeBridgeViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {
    BackHandler(onBack = onFinished)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0A06), CozyBlack, Color(0xFF0A0505))
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        // Celebration overlay
        AnimatedVisibility(
            visible = uiState.isComplete && uiState.survived,
            enter   = fadeIn() + scaleIn(),
            exit    = fadeOut()
        ) {
            SurvivedCelebration(onDone = onFinished)
        }

        if (!uiState.isComplete || !uiState.survived) {
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
                    onClick = onFinished,
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(58.dp))

                Text(
                    "Surge Bridge",
                    style      = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = Caveat,
                        fontSize   = 36.sp
                    ),
                    color      = SurgeRed
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Hold on. The craving peak lasts 90 seconds.",
                    style     = MaterialTheme.typography.bodyMedium,
                    color     = SoftTan,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(28.dp))

                // ── Countdown ring ──────────────────────────────────────────
                SurgeCountdownRing(
                    seconds   = uiState.secondsRemaining,
                    total     = SURGE_TOTAL,
                    isRunning = uiState.isRunning
                )

                Spacer(Modifier.height(28.dp))

                // ── Method tabs ─────────────────────────────────────────────
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CozyBlack3),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SurgeTab.entries.forEach { tab ->
                        val label = when (tab) {
                            SurgeTab.BREATHING -> "Breathe"
                            SurgeTab.CBT       -> "Journal"
                            SurgeTab.PHYSICAL  -> "Move"
                        }
                        val selected = tab == uiState.activeTab
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier         = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (selected) SurgeRed.copy(alpha = 0.18f) else Color.Transparent)
                                .clickable { viewModel.setTab(tab) }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                label,
                                style      = MaterialTheme.typography.labelMedium,
                                color      = if (selected) SurgeRed else SoftTan,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                textAlign  = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // ── Tab content ─────────────────────────────────────────────
                AnimatedContent(
                    targetState   = uiState.activeTab,
                    transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
                    label         = "surge_tab_content",
                    modifier      = Modifier.weight(1f)
                ) { tab ->
                    when (tab) {
                        SurgeTab.BREATHING -> BreathingGuide(uiState.breathPhase, uiState.breathSeconds)
                        SurgeTab.CBT       -> CbtJournal(uiState.cbtEmotion, viewModel::onCbtEmotionChange)
                        SurgeTab.PHYSICAL  -> PhysicalReplacement(uiState.physicalSuggestion)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Action buttons ──────────────────────────────────────────
                Button(
                    onClick  = { viewModel.markSurvived(onFinished) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape    = RoundedCornerShape(18.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = MossGreen,
                        contentColor   = CozyBlack
                    )
                ) {
                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("I Survived the Urge", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(Modifier.height(10.dp))

                TextButton(
                    onClick  = { viewModel.markYielded(onFinished) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I gave in — log it anyway", color = DimTan, fontSize = 13.sp)
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Countdown Ring
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SurgeCountdownRing(seconds: Int, total: Float, isRunning: Boolean) {
    val progress = seconds / total
    val sweepAngle by animateFloatAsState(
        targetValue   = progress * 360f,
        animationSpec = tween(900, easing = LinearEasing),
        label         = "surge_sweep"
    )
    val ringColor = when {
        progress > 0.6f -> SurgeRed
        progress > 0.3f -> CozyAmber
        else            -> MossGreen
    }
    val pulseScale by rememberInfiniteTransition(label = "surge_pulse")
        .animateFloat(
            initialValue  = 1f,
            targetValue   = if (isRunning) 1.05f else 1f,
            animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
            label         = "surge_scale"
        )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(180.dp).scale(pulseScale)) {
        Canvas(modifier = Modifier.size(180.dp)) {
            // Background track
            drawArc(
                color      = CozyBlack3,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter  = false,
                style      = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
            )
            // Progress arc
            drawArc(
                color      = ringColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter  = false,
                style      = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "$seconds",
                style      = MaterialTheme.typography.displayMedium,
                color      = CreamWhite,
                fontWeight = FontWeight.Bold,
                fontSize   = 52.sp
            )
            Text("seconds", style = MaterialTheme.typography.labelSmall, color = SoftTan)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Breathing guide (Box Breathing 4-4-4-4)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BreathingGuide(phase: BreathPhase, secondsLeft: Int) {
    val circleScale by animateFloatAsState(
        targetValue   = when (phase) {
            BreathPhase.INHALE -> 1.3f
            BreathPhase.HOLD1  -> 1.3f
            BreathPhase.EXHALE -> 0.85f
            BreathPhase.HOLD2  -> 0.85f
        },
        animationSpec = tween(800, easing = EaseInOutSine),
        label         = "breath_scale"
    )

    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier.size(180.dp)
        ) {
            // Outer glow
            Box(
                modifier = Modifier
                    .size((140 * circleScale).dp)
                    .background(
                        Brush.radialGradient(listOf(SurgeRed.copy(alpha = 0.15f), Color.Transparent)),
                        CircleShape
                    )
            )
            // Inner circle
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .size((100 * circleScale).dp)
                    .background(SurgeRed.copy(alpha = 0.20f), CircleShape)
                    .border(2.dp, SurgeRed.copy(alpha = 0.6f), CircleShape)
            ) {
                Text(secondsLeft.toString(), fontSize = 28.sp, color = CreamWhite, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(phase.label, style = MaterialTheme.typography.headlineSmall,
            color = SurgeRed, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("4 in · 4 hold · 4 out · 4 hold", style = MaterialTheme.typography.bodySmall, color = SoftTan)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CBT Journal
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun CbtJournal(value: String, onChange: (String) -> Unit) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.yutorikaizen.R.drawable.ribi_journal),
            contentDescription = "Journaling Frog",
            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp)),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Spacer(Modifier.height(16.dp))
        Surface(
            color = CozyBlack3,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "\"Name the emotion you feel in your body right now.\"",
                    style     = MaterialTheme.typography.bodyLarge,
                    color     = CreamWhite,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Where is it? Chest? Throat? Hands?",
                    style     = MaterialTheme.typography.bodySmall,
                    color     = SoftTan,
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value         = value,
            onValueChange = onChange,
            placeholder   = { Text("I feel… in my…", color = DimTan) },
            modifier      = Modifier.fillMaxWidth().height(180.dp),
            shape         = RoundedCornerShape(14.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = SurgeRed,
                unfocusedBorderColor = CozyBorder,
                focusedTextColor     = CreamWhite,
                unfocusedTextColor   = CreamWhite,
                cursorColor          = SurgeRed
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Physical Replacement
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PhysicalReplacement(suggestion: String) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.yutorikaizen.R.drawable.ribi_physical),
            contentDescription = "Physical Frog",
            modifier = Modifier.size(120.dp).clip(CircleShape).border(2.dp, SurgeRed.copy(alpha = 0.4f), CircleShape),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "Do this right now:",
            style  = MaterialTheme.typography.labelLarge,
            color  = SoftTan
        )
        Spacer(Modifier.height(12.dp))
        Text(
            suggestion,
            style     = MaterialTheme.typography.titleLarge,
            color     = CreamWhite,
            fontWeight = FontWeight.Bold,
            textAlign  = TextAlign.Center,
            lineHeight = 30.sp
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Cravings peak at 90 seconds then dissolve.\nYour body is your reset button.",
            style     = MaterialTheme.typography.bodySmall,
            color     = SoftTan,
            textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Survived Celebration
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SurvivedCelebration(onDone: () -> Unit) {
    val scale by rememberInfiniteTransition(label = "celebrate")
        .animateFloat(
            initialValue  = 1f,
            targetValue   = 1.05f,
            animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
            label         = "celebrate_scale"
        )

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(listOf(MossGreenDim, CozyBlack)),
                shape = RoundedCornerShape(0.dp)
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.yutorikaizen.R.drawable.ribi_celebration),
            contentDescription = "Celebration Frog",
            modifier = Modifier.size(200.dp).scale(scale).clip(RoundedCornerShape(24.dp)),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Spacer(Modifier.height(32.dp))
        Text("Surge Resisted!", style = MaterialTheme.typography.headlineLarge,
            color = MossGreen, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Text(
            "Your dopamine receptors just got a little bit stronger. Every resistance is a neural rewiring.",
            style     = MaterialTheme.typography.bodyLarge,
            color     = WarmCream,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick  = onDone,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape    = RoundedCornerShape(18.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = MossGreen, contentColor = CozyBlack)
        ) {
            Text("Back to Journey", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

