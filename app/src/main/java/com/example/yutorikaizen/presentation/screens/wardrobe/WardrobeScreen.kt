package com.example.yutorikaizen.presentation.screens.wardrobe

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.domain.model.FrogSkin
import com.example.yutorikaizen.presentation.screens.account.AccountViewModel
import com.example.yutorikaizen.presentation.theme.*
import com.example.yutorikaizen.presentation.components.tourTarget
import kotlinx.coroutines.launch

@Composable
fun WardrobeScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    BackHandler(onBack = onBack)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val selectedSkin = uiState.skins.find { it.id == uiState.selectedSkinId }

    Scaffold(
        containerColor = CozyBlack,
        snackbarHost = {
            SnackbarHost(hostState = snackbarState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CozyBlack3,
                    contentColor = CreamWhite,
                    actionColor = MossGreen,
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CozyBlack)
        ) {
            // ── Top Bar ─────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = SoftTan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Frog Wardrobe",
                        style = MaterialTheme.typography.titleLarge,
                        color = CreamWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Customize Ribi's look in your pond",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftTan
                    )
                }
            }

            // ── Hero Equipped Preview ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .tourTarget("wardrobe_preview")
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                (selectedSkin?.accentColor ?: MossGreen).copy(alpha = 0.18f),
                                CozyBlack2
                            )
                        )
                    )
                    .border(
                        1.dp,
                        (selectedSkin?.accentColor ?: MossGreen).copy(alpha = 0.4f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val infiniteTransition = rememberInfiniteTransition(label = "hero_pulse")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.06f,
                        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
                        label = "hero_scale"
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .scale(pulseScale)
                            .background(
                                (selectedSkin?.accentColor ?: MossGreen).copy(alpha = 0.25f),
                                CircleShape
                            )
                            .border(2.dp, selectedSkin?.accentColor ?: MossGreen, CircleShape)
                    ) {
                        Text(selectedSkin?.emoji ?: "🐸", fontSize = 42.sp)
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = selectedSkin?.displayName ?: "Classic",
                        style = MaterialTheme.typography.titleMedium,
                        color = CreamWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedSkin?.description ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = SoftTan,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = (selectedSkin?.accentColor ?: MossGreen).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "✓ Currently Equipped",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = selectedSkin?.accentColor ?: MossGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Grid of Skins ───────────────────────────────────────────────────
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.skins, key = { it.id }) { skin ->
                    val isSelected = skin.id == uiState.selectedSkinId

                    Card(
                        onClick = {
                            viewModel.selectSkin(skin)
                            scope.launch {
                                snackbarState.showSnackbar(
                                    message = "${skin.emoji} ${skin.displayName} equipped!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) skin.accentColor.copy(alpha = 0.12f) else CozyBlack2
                        ),
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) skin.accentColor else CozyBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(
                                        if (isSelected) skin.accentColor.copy(alpha = 0.2f) else CozyBlack3,
                                        CircleShape
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) skin.accentColor else CozyBorder,
                                        CircleShape
                                    )
                            ) {
                                Text(skin.emoji, fontSize = 28.sp)
                            }

                            Text(
                                text = skin.displayName,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isSelected) skin.accentColor else CreamWhite,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = skin.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = SoftTan,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                minLines = 2
                            )

                            if (isSelected) {
                                Surface(
                                    color = skin.accentColor,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = CozyBlack,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "Equipped",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CozyBlack,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.selectSkin(skin)
                                        scope.launch {
                                            snackbarState.showSnackbar(
                                                message = "${skin.emoji} ${skin.displayName} equipped!",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(32.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, CozyBorder),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = "Equip",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SoftTan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
