package com.example.yutorikaizen.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.presentation.theme.*
import kotlin.math.roundToInt

/**
 * Home is the frog's world — minimal UI, maximum frog visibility.
 * Only a floating greeting chip at the top and a soft action cluster
 * at the bottom. Everything else is transparent so the Godot frog shines.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onStartFocusSession: () -> Unit = {},
    onDuckBreak: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // ── Top greeting chip ──────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 24.dp, end = 24.dp),
            color  = CozyBlack.copy(alpha = 0.72f),
            shape  = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🐸", fontSize = 18.sp)
                Text(
                    text  = uiState.greeting,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CreamWhite,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // ── Score badge ────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 112.dp, end = 24.dp),
            color = CozyBlack.copy(alpha = 0.72f),
            shape = androidx.compose.foundation.shape.CircleShape
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text  = "${(uiState.consistencyScore * 100).roundToInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = MossGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text  = "streak",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmCream.copy(alpha = 0.6f)
                )
            }
        }

        // ── Bottom action cluster ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick  = onStartFocusSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape  = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MossGreen,
                    contentColor   = CozyBlack
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Text(
                    text       = "▶  Start Focus",
                    style      = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            OutlinedButton(
                onClick  = onDuckBreak,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape  = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, WarmCream.copy(alpha = 0.3f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = CozyBlack.copy(alpha = 0.55f),
                    contentColor   = WarmCream
                )
            ) {
                Text(
                    text  = "🐸  Frog Break",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
