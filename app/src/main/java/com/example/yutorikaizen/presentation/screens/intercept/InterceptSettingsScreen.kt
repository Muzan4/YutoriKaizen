package com.example.yutorikaizen.presentation.screens.intercept

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.data.local.entity.InterceptConfigEntity
import com.example.yutorikaizen.domain.model.SocialMediaApps
import com.example.yutorikaizen.presentation.theme.*
import com.example.yutorikaizen.presentation.components.tourTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterceptSettingsScreen(
    onBack: () -> Unit,
    viewModel: InterceptSettingsViewModel = hiltViewModel()
) {
    BackHandler(onBack = onBack)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context  = LocalContext.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = CozyBlack,
        topBar = {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = SoftTan)
                }
                Text(
                    "App Intercept",
                    style      = MaterialTheme.typography.headlineSmall,
                    color      = CreamWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(48.dp))
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── App Pause Hero Card ───────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .tourTarget("app_pause_button"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CozyBlack2),
                border = BorderStroke(1.5.dp, MossGreen.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MossGreen.copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Shield, contentDescription = null, tint = MossGreen, modifier = Modifier.size(24.dp))
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "App Pause Protection",
                            style = MaterialTheme.typography.titleMedium,
                            color = CreamWhite,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Mindful 5s breathing pause before opening distracting apps",
                            style = MaterialTheme.typography.bodySmall,
                            color = SoftTan
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // ── Accessibility service banner ──────────────────────────────
            AccessibilityBanner(
                onOpenSettings = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            )

            Spacer(Modifier.height(28.dp))

            // ── Active rules ──────────────────────────────────────────────
            if (uiState.configs.isNotEmpty()) {
                Text(
                    "Your rules",
                    style      = MaterialTheme.typography.labelLarge,
                    color      = MossGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(10.dp))

                uiState.configs.forEach { config ->
                    InterceptRuleCard(
                        config    = config,
                        onToggle  = { viewModel.toggleEnabled(config) },
                        onEdit    = { viewModel.openEditSheet(config) },
                        onDelete  = { viewModel.deleteConfig(config) }
                    )
                    Spacer(Modifier.height(8.dp))
                }

                Spacer(Modifier.height(20.dp))
            }

            // ── Search & Installed Apps ────────────────────────────────────
            Text(
                "Choose Any Installed App",
                style      = MaterialTheme.typography.labelLarge,
                color      = MossGreen,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("Search installed apps...", color = DimTan, style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = SoftTan) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = SoftTan)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MossGreen,
                    unfocusedBorderColor = CozyBorder,
                    focusedTextColor = CreamWhite,
                    unfocusedTextColor = CreamWhite,
                    cursorColor = MossGreen
                )
            )

            Spacer(Modifier.height(12.dp))

            if (uiState.isLoadingApps) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MossGreen, modifier = Modifier.size(28.dp))
                }
            } else {
                val availableApps = viewModel.filteredAvailableApps
                if (availableApps.isEmpty()) {
                    Text(
                        if (uiState.searchQuery.isEmpty()) "All installed apps already added!" else "No matching apps found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SoftTan,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    availableApps.forEach { item ->
                        val emoji = if (item.isSocial) SocialMediaApps.getEmoji(item.packageName) else "📱"
                        SuggestedAppRow(
                            emoji = emoji,
                            label = item.label,
                            packageName = item.packageName,
                            onAdd = { viewModel.openAddSheet(item.packageName, item.label) }
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // ── Add / Edit bottom sheet ───────────────────────────────────────────────
    if (uiState.showSheet) {
        ModalBottomSheet(
            onDismissRequest   = { viewModel.dismissSheet() },
            sheetState         = sheetState,
            containerColor     = CozyBlack3,
            dragHandle         = {
                Box(
                    modifier = Modifier
                        .padding(top = 14.dp, bottom = 8.dp)
                        .size(width = 36.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(CozyBorder)
                )
            }
        ) {
            InterceptEditSheet(
                appLabel   = uiState.editingLabel,
                prompt     = uiState.editingPrompt,
                delay      = uiState.editingDelay,
                onPrompt   = viewModel::onPromptChange,
                onDelay    = viewModel::onDelayChange,
                onSave     = viewModel::saveConfig,
                onDismiss  = viewModel::dismissSheet
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AccessibilityBanner(onOpenSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF0D1F0D), MossGreenDim)
                )
            )
            .border(1.dp, MossGreenDim, RoundedCornerShape(16.dp))
            .clickable(onClick = onOpenSettings)
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(Icons.Rounded.Accessibility, contentDescription = null, tint = MossGreen, modifier = Modifier.size(28.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Enable Accessibility Permission",
                style      = MaterialTheme.typography.titleSmall,
                color      = CreamWhite,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Required to pause selected apps. Tap to open system settings.",
                style = MaterialTheme.typography.bodySmall,
                color = SoftTan
            )
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = SoftTan)
    }
}

@Composable
private fun InterceptRuleCard(
    config: InterceptConfigEntity,
    onToggle: () -> Unit,
    onEdit:   () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CozyBlack4)
            .border(1.dp, CozyBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onEdit)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            SocialMediaApps.getEmoji(config.packageName),
            fontSize = 22.sp,
            modifier = Modifier.size(32.dp),
            textAlign = TextAlign.Center
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                config.appLabel,
                style      = MaterialTheme.typography.titleSmall,
                color      = CreamWhite,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "\"${config.prompt.take(48)}${if (config.prompt.length > 48) "…" else ""}\"",
                style = MaterialTheme.typography.bodySmall,
                color = SoftTan
            )
            Text(
                "${config.delaySeconds}s countdown",
                style = MaterialTheme.typography.labelSmall,
                color = DimTan
            )
        }

        Switch(
            checked         = config.enabled,
            onCheckedChange = { onToggle() },
            colors          = SwitchDefaults.colors(
                checkedThumbColor   = MossGreen,
                checkedTrackColor   = MossGreenDim,
                uncheckedTrackColor = CozyBlack3
            )
        )

        IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Rounded.DeleteOutline, contentDescription = "Remove", tint = DimTan, modifier = Modifier.size(18.dp))
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest  = { showDeleteConfirm = false },
            containerColor    = CozyBlack3,
            title             = {
                Text("Remove ${config.appLabel}?", style = MaterialTheme.typography.titleMedium, color = CreamWhite)
            },
            text              = {
                Text("This will stop intercepting ${config.appLabel}.", style = MaterialTheme.typography.bodyMedium, color = SoftTan)
            },
            confirmButton     = {
                TextButton(onClick = { showDeleteConfirm = false; onDelete() }) {
                    Text("Remove", color = SurgeRed)
                }
            },
            dismissButton     = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = SoftTan)
                }
            }
        )
    }
}

@Composable
private fun SuggestedAppRow(
    emoji: String,
    label: String,
    packageName: String,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CozyBlack3)
            .border(1.dp, CozyBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onAdd)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = WarmCream)
            Text(packageName, style = MaterialTheme.typography.labelSmall, color = DimTan)
        }
        Icon(Icons.Rounded.Add, contentDescription = "Add", tint = MossGreen, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun InterceptEditSheet(
    appLabel: String,
    prompt:   String,
    delay:    Int,
    onPrompt: (String) -> Unit,
    onDelay:  (Int)    -> Unit,
    onSave:   ()       -> Unit,
    onDismiss: ()      -> Unit
) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Set pause for $appLabel",
            style      = MaterialTheme.typography.headlineMedium.copy(fontFamily = Fraunces),
            color      = CreamWhite,
            fontWeight = FontWeight.Bold
        )

        // ── Prompt text field ─────────────────────────────────────────────
        OutlinedTextField(
            value         = prompt,
            onValueChange = onPrompt,
            label         = {
                Text(
                    "Your message to yourself",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftTan
                )
            },
            placeholder   = {
                Text(
                    "e.g. Do I really need to check this right now?",
                    style = MaterialTheme.typography.bodySmall,
                    color = DimTan
                )
            },
            minLines      = 3,
            maxLines      = 5,
            modifier      = Modifier.fillMaxWidth(),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MossGreen,
                unfocusedBorderColor = CozyBorder,
                focusedTextColor     = CreamWhite,
                unfocusedTextColor   = CreamWhite,
                cursorColor          = MossGreen,
                focusedLabelColor    = MossGreen
            ),
            textStyle     = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = Caveat,
                fontSize   = 18.sp,
                color      = CreamWhite
            )
        )

        // ── Countdown delay selector ──────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Pause duration: ${delay}s",
                style = MaterialTheme.typography.labelMedium,
                color = SoftTan
            )
            Slider(
                value         = delay.toFloat(),
                onValueChange = { onDelay(it.toInt()) },
                valueRange    = 3f..15f,
                steps         = 11,
                colors        = SliderDefaults.colors(
                    thumbColor            = MossGreen,
                    activeTrackColor      = MossGreen,
                    inactiveTrackColor    = CozyBorder
                )
            )
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("3s", style = MaterialTheme.typography.labelSmall, color = DimTan)
                Text("15s", style = MaterialTheme.typography.labelSmall, color = DimTan)
            }
        }

        // ── Action buttons ────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick  = onDismiss,
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = SoftTan),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }

            Button(
                onClick  = onSave,
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MossGreen,
                    contentColor   = CozyBlack
                ),
                modifier = Modifier.weight(1f).height(48.dp),
                enabled  = prompt.isNotBlank()
            ) {
                Text("Save rule", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
