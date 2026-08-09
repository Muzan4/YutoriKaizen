package com.example.yutorikaizen.presentation.screens.duckbreak

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.presentation.theme.*

@Composable
fun FrogBreakScreen(viewModel: FrogBreakViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier         = Modifier.fillMaxSize().background(CozyBlack),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isDone                                   -> BreakDonePanel(onReset = { viewModel.reset() })
            uiState.selectedOption == null                   -> BreakOptionPicker(onOption = { viewModel.selectOption(it) })
            uiState.selectedOption == FrogBreakOption.BREATHING -> BreathingPanel(uiState)
            uiState.selectedOption == FrogBreakOption.JOURNAL   -> JournalPanel(
                text     = uiState.journalText,
                onChange = { viewModel.onJournalChange(it) },
                onDone   = { viewModel.markDone() }
            )
            uiState.selectedOption == FrogBreakOption.PHYSICAL  -> PhysicalPanel(
                nudge  = uiState.physicalNudge,
                onDone = { viewModel.markDone() }
            )
        }
    }
}

@Composable
private fun BreakOptionPicker(onOption: (FrogBreakOption) -> Unit) {
    Column(
        modifier            = Modifier.padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("🐸", style = MaterialTheme.typography.displayLarge)
        Text(
            text      = "Take a cozy break\nwith your frog",
            style     = MaterialTheme.typography.headlineMedium,
            color     = CreamWhite,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        BreakCard("🌬️", "Breathe",  "60 seconds of calm breathing") { onOption(FrogBreakOption.BREATHING) }
        BreakCard("📝", "Journal",  "Write one thing you're feeling")   { onOption(FrogBreakOption.JOURNAL) }
        BreakCard("🚶", "Move",     "A small physical reset")           { onOption(FrogBreakOption.PHYSICAL) }
    }
}

@Composable
private fun BreakCard(emoji: String, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick  = onClick,
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = CozyBlack3),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier              = Modifier.padding(18.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color  = CozyBlack4,
                shape  = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(emoji, style = MaterialTheme.typography.titleLarge)
                }
            }
            Column {
                Text(title,    style = MaterialTheme.typography.labelLarge,  color = CreamWhite, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,   color = SoftTan)
            }
        }
    }
}

@Composable
private fun BreathingPanel(uiState: FrogBreakUiState) {
    val targetScale = when (uiState.breathPhase) {
        BreathPhase.INHALE, BreathPhase.HOLD_IN  -> 1.3f
        BreathPhase.EXHALE, BreathPhase.HOLD_OUT -> 0.85f
    }
    val scale by animateFloatAsState(
        targetValue   = targetScale,
        animationSpec = tween(durationMillis = 1200, easing = EaseInOutSine),
        label         = "breath_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp),
        modifier            = Modifier.padding(28.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(220.dp)
                .scale(scale)
                .background(MossGlow, CircleShape)
                .border(2.dp, MossGreen, CircleShape)
        ) {
            Text("🐸", style = MaterialTheme.typography.displayLarge)
        }

        Text(
            text       = uiState.breathLabel,
            style      = MaterialTheme.typography.headlineMedium,
            color      = CreamWhite,
            fontWeight = FontWeight.Bold
        )

        LinearProgressIndicator(
            progress   = { uiState.breathProgress },
            modifier   = Modifier.fillMaxWidth(0.6f),
            color      = MossGreen,
            trackColor = CozyBlack3,
        )
    }
}

@Composable
private fun JournalPanel(text: String, onChange: (String) -> Unit, onDone: () -> Unit) {
    Column(
        modifier            = Modifier.padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("📝", style = MaterialTheme.typography.displayLarge)
        Text(
            "What are you feeling\nright now?",
            style      = MaterialTheme.typography.headlineMedium,
            color      = CreamWhite,
            textAlign  = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value         = text,
            onValueChange = onChange,
            placeholder   = { Text("Just one honest line…", color = DimTan) },
            modifier      = Modifier.fillMaxWidth().height(130.dp),
            shape         = RoundedCornerShape(14.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MossGreen,
                unfocusedBorderColor = CozyBorder,
                focusedTextColor     = CreamWhite,
                unfocusedTextColor   = CreamWhite,
                cursorColor          = MossGreen
            )
        )
        Button(
            onClick  = onDone,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = MossGreen, contentColor = CozyBlack)
        ) {
            Text("Done", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PhysicalPanel(nudge: String, onDone: () -> Unit) {
    Column(
        modifier            = Modifier.padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("🚶", style = MaterialTheme.typography.displayLarge)
        Text(
            "Small move",
            style      = MaterialTheme.typography.headlineMedium,
            color      = CreamWhite,
            fontWeight = FontWeight.Bold
        )
        Card(
            shape    = RoundedCornerShape(20.dp),
            colors   = CardDefaults.cardColors(containerColor = CozyBlack3),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text      = nudge,
                style     = MaterialTheme.typography.bodyLarge,
                color     = WarmCream,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(24.dp)
            )
        }
        Button(
            onClick  = onDone,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = MossGreen, contentColor = CozyBlack)
        ) {
            Text("Done ✓", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BreakDonePanel(onReset: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier            = Modifier.padding(28.dp)
    ) {
        Text("🎉", style = MaterialTheme.typography.displayLarge)
        Text(
            "Nice reset! 🌙",
            style      = MaterialTheme.typography.headlineMedium,
            color      = CreamWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Your frog is proud of you.",
            style = MaterialTheme.typography.bodyLarge,
            color = SoftTan
        )
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onReset) {
            Text("Take another break", color = MossGreen, fontWeight = FontWeight.Medium)
        }
    }
}
