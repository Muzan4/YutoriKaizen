package com.example.yutorikaizen.presentation.screens.duckbreak

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.R
import com.example.yutorikaizen.presentation.theme.*

@Composable
fun FrogBreakScreen(
    onBack: () -> Unit = {},
    viewModel: FrogBreakViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val handleBack: () -> Unit = {
        if (uiState.selectedOption != null || uiState.isDone) {
            viewModel.reset()
        } else {
            onBack()
        }
    }

    BackHandler(onBack = handleBack)

    Box(
        modifier         = Modifier.fillMaxSize().background(CozyBlack),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isDone                                   -> BreakDonePanel(
                onReset = { viewModel.reset() },
                onBack  = onBack
            )
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

        // Top back bar - always visible so user never gets stuck
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = handleBack,
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
            Spacer(Modifier.width(12.dp))
            Text(
                "Recovery Break",
                style = MaterialTheme.typography.titleMedium,
                color = SoftTan,
                fontWeight = FontWeight.Medium
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
        Image(
            painter = painterResource(id = R.drawable.ic_ribi_head),
            contentDescription = "Ribi",
            modifier = Modifier.size(60.dp).clip(CircleShape)
        )
        Text(
            text      = "Mindful Recovery Break",
            style     = MaterialTheme.typography.headlineMedium,
            color     = CreamWhite,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        BreakCard(Icons.Rounded.Air, "Breathe",  "60 seconds of calm physiological breathing") { onOption(FrogBreakOption.BREATHING) }
        BreakCard(Icons.Rounded.EditNote, "Journal",  "Write one honest line of what you feel")   { onOption(FrogBreakOption.JOURNAL) }
        BreakCard(Icons.Rounded.DirectionsWalk, "Move",     "A quick physical posture reset")           { onOption(FrogBreakOption.PHYSICAL) }
    }
}

@Composable
private fun BreakCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
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
                color  = MossGreen.copy(alpha = 0.15f),
                shape  = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(icon, contentDescription = null, tint = MossGreen, modifier = Modifier.size(24.dp))
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
            Image(
                painter = painterResource(id = R.drawable.ic_ribi_head),
                contentDescription = "Ribi",
                modifier = Modifier.size(80.dp).clip(CircleShape)
            )
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
        Icon(Icons.Rounded.EditNote, contentDescription = null, tint = MossGreen, modifier = Modifier.size(48.dp))
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
        Icon(Icons.Rounded.DirectionsWalk, contentDescription = null, tint = MossGreen, modifier = Modifier.size(48.dp))
        Text(
            "Physical Reset",
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
            Text("Complete", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BreakDonePanel(
    onReset: () -> Unit,
    onBack: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier            = Modifier.padding(28.dp)
    ) {
        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MossGreen, modifier = Modifier.size(54.dp))
        Text(
            "Reset Complete",
            style      = MaterialTheme.typography.headlineMedium,
            color      = CreamWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Take a deep breath and return with clarity.",
            style = MaterialTheme.typography.bodyLarge,
            color = SoftTan
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick  = onBack,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(containerColor = MossGreen, contentColor = CozyBlack)
        ) {
            Text("Back to Previous Page", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
        TextButton(onClick = onReset) {
            Text("Take another break", color = SoftTan, fontWeight = FontWeight.Medium)
        }
    }
}
