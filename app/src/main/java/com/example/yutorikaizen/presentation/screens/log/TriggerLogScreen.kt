package com.example.yutorikaizen.presentation.screens.log

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.domain.model.TriggerLog
import com.example.yutorikaizen.presentation.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TriggerLogScreen(viewModel: TriggerLogViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyBlack)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Text(
            text       = "Urge Log",
            style      = MaterialTheme.typography.headlineMedium,
            color      = CreamWhite,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text  = "What pulled you away?",
            style = MaterialTheme.typography.bodyMedium,
            color = SoftTan
        )

        Spacer(Modifier.height(24.dp))

        // Emotion chips
        Text(
            text  = "I'm feeling…",
            style = MaterialTheme.typography.labelLarge,
            color = SoftTan
        )
        Spacer(Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.fillMaxWidth()
        ) {
            EMOTIONS.forEach { emotion ->
                val selected = uiState.selectedEmotion == emotion
                FilterChip(
                    selected = selected,
                    onClick  = { viewModel.selectEmotion(emotion) },
                    label    = { Text(emotion, style = MaterialTheme.typography.labelSmall) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor    = MossGreenDim,
                        selectedLabelColor        = MossGreen,
                        containerColor            = CozyBlack4,
                        labelColor                = SoftTan
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value         = uiState.note,
            onValueChange = { viewModel.onNoteChange(it) },
            placeholder   = { Text("Add a note (optional)", color = DimTan) },
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(14.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MossGreen,
                unfocusedBorderColor = CozyBorder,
                focusedTextColor     = CreamWhite,
                unfocusedTextColor   = CreamWhite,
                cursorColor          = MossGreen
            ),
            maxLines = 3
        )

        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(visible = uiState.selectedEmotion != null) {
            Button(
                onClick  = { viewModel.saveLog() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MossGreen,
                    contentColor   = CozyBlack
                )
            ) {
                Text(
                    text       = if (uiState.justSaved) "Logged ✓" else "Log this feeling",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        if (uiState.logs.isNotEmpty()) {
            Text(
                text  = "Recent logs",
                style = MaterialTheme.typography.labelLarge,
                color = SoftTan,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.logs.take(20), key = { it.id }) { log ->
                    TriggerLogRow(log)
                }
            }
        }
    }
}

@Composable
private fun TriggerLogRow(log: TriggerLog) {
    val fmt  = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }
    val time = remember(log.timestamp) { fmt.format(Date(log.timestamp)) }

    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack3)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = log.emotion.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelLarge,
                    color = CozyAmber,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text  = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftTan
                )
            }
            if (!log.note.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = log.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmCream.copy(alpha = 0.8f)
                )
            }
        }
    }
}
