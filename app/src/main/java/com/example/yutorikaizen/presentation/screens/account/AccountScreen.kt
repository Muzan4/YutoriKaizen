package com.example.yutorikaizen.presentation.screens.account

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.domain.model.FrogSkin
import com.example.yutorikaizen.presentation.theme.*
import com.example.yutorikaizen.presentation.components.tourTarget
import com.example.yutorikaizen.service.CatOverlayPreferences
import com.example.yutorikaizen.service.CatOverlayService
import kotlin.math.roundToInt

@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onInterceptSettings: () -> Unit = {},
    onWardrobeClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {},
    onAnalyticsClick: () -> Unit = {}
) {
    BackHandler(onBack = onBack)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.checkUsagePermissionOnResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = CozyBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CozyBlack)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // ── Top Bar ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = SoftTan,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = "Profile & Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Profile Header Card ─────────────────────────────────────────
            val selectedSkin = uiState.skins.find { it.id == uiState.selectedSkinId }
            Box(modifier = Modifier.fillMaxWidth().tourTarget("profile_header_card")) {
                ProfileHeaderCard(
                    skin = selectedSkin,
                    dayNumber = uiState.dayNumber,
                    phaseNumber = uiState.phaseNumber,
                    difficultyLabel = uiState.difficultyLabel,
                    nciScore = uiState.nciScore,
                    currentStreak = uiState.currentStreak
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Feature Pages Hub ──────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Features & Tools",
                    style = MaterialTheme.typography.titleSmall,
                    color = SoftTan,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 1. Frog Wardrobe Card
                FeatureNavCard(
                    icon = Icons.Rounded.Checkroom,
                    title = "Frog Wardrobe",
                    subtitle = "Equipped: ${selectedSkin?.displayName ?: "Classic"}",
                    badge = "${uiState.skins.size} skins",
                    accentColor = selectedSkin?.accentColor ?: MossGreen,
                    onClick = onWardrobeClick
                )

                Spacer(Modifier.height(10.dp))

                // 2. Expense Tracker Card
                FeatureNavCard(
                    icon = Icons.Rounded.AccountBalanceWallet,
                    title = "Expense Tracker",
                    subtitle = if (uiState.monthlyTotal > 0) "₹${"%.0f".format(uiState.monthlyTotal)} logged this month" else "Track mindful spending",
                    badge = if (uiState.expenses.isNotEmpty()) "${uiState.expenses.size} entries" else null,
                    accentColor = CozyAmber,
                    onClick = onExpensesClick
                )

                Spacer(Modifier.height(10.dp))

                // 3. App Pause / Intercept
                FeatureNavCard(
                    icon = Icons.Rounded.Shield,
                    title = "App Pause (One Sec)",
                    subtitle = "Breath delay before opening doomscrolling apps",
                    badge = "ACTIVE",
                    accentColor = Phase2Blue,
                    onClick = onInterceptSettings
                )

                Spacer(Modifier.height(10.dp))

                // 4. Insights & Analytics
                FeatureNavCard(
                    icon = Icons.Rounded.QueryStats,
                    title = "Daily Insight",
                    subtitle = "Consistency score, urge logs & recovery progress",
                    badge = "${(uiState.nciScore * 100).toInt()}% SCORE",
                    accentColor = Phase4Purple,
                    onClick = onAnalyticsClick
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── App Settings ───────────────────────────────────────────────
            // Cat overlay toggle: read/write from SharedPrefs, start/stop service
            var catOverlayEnabled by remember {
                mutableStateOf(CatOverlayPreferences.isAutoHideEnabled(context))
            }
            // We track service running state via a separate SharedPref key set by service
            val catServiceRunning = remember {
                mutableStateOf(
                    android.app.ActivityManager.RunningServiceInfo::class.java.let { false } // init
                )
            }
            // Check if cat overlay pref is enabled (service-started state)
            var catServiceActive by remember {
                mutableStateOf(
                    context.getSharedPreferences("cat_overlay_prefs", android.content.Context.MODE_PRIVATE)
                        .getBoolean("cat_service_active", false)
                )
            }

            SettingsSection(
                usageTrackingEnabled = uiState.usageTrackingEnabled,
                onUsageTrackingToggle = { enabled ->
                    viewModel.toggleUsageTracking(enabled)
                    if (enabled) {
                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }
                },
                notificationsEnabled = uiState.notificationsEnabled,
                onNotificationsToggle = { viewModel.toggleNotifications(it) },
                catOverlayActive = catServiceActive,
                onCatOverlayToggle = { enable ->
                    catServiceActive = enable
                    context.getSharedPreferences("cat_overlay_prefs", android.content.Context.MODE_PRIVATE)
                        .edit().putBoolean("cat_service_active", enable).apply()
                    if (enable) {
                        // Check for overlay permission first
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            !Settings.canDrawOverlays(context)) {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    android.net.Uri.parse("package:${context.packageName}")
                                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                            )
                        } else {
                            CatOverlayService.start(context)
                        }
                    } else {
                        CatOverlayService.stop(context)
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            // ── Danger Zone ────────────────────────────────────────────────
            DangerZoneSection(
                onResetJourneyClick = { viewModel.showResetJourneyConfirmation() },
                onDeleteClick = { viewModel.showDeleteConfirmation() }
            )

            Spacer(Modifier.height(24.dp))

            // ── Footer ─────────────────────────────────────────────────────
            Text(
                text = "v2.0 · YutoriKaizen · Mindful Living",
                style = MaterialTheme.typography.labelSmall,
                color = DimTan,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }

    if (uiState.showResetJourneyConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissResetJourneyConfirmation() },
            containerColor = CozyBlack2,
            title = {
                Text(
                    text = "Reset Journey to Day 1?",
                    color = CozyAmber,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will reset your current journey back to Day 1 (Phase 1: Reset & Detox) and clear your journey check-ins.",
                    color = CreamWhite
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmResetJourney()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CozyAmber)
                ) {
                    Text("Reset to Day 1", color = CozyBlack)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissResetJourneyConfirmation() }) {
                    Text("Cancel", color = SoftTan)
                }
            }
        )
    }

    if (uiState.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirmation() },
            containerColor = CozyBlack2,
            title = {
                Text(
                    text = "Reset All Progress?",
                    color = SurgeRed,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will delete all your check-ins, surge events, habits, and settings. This cannot be undone.",
                    color = CreamWhite
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount {
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurgeRed)
                ) {
                    Text("Delete Everything", color = CozyBlack)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteConfirmation() }) {
                    Text("Cancel", color = SoftTan)
                }
            }
        )
    }
}

@Composable
private fun ProfileHeaderCard(
    skin: FrogSkin?,
    dayNumber: Int,
    phaseNumber: Int,
    difficultyLabel: String,
    nciScore: Float,
    currentStreak: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack2),
        border = BorderStroke(1.dp, (skin?.accentColor ?: MossGreen).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                (skin?.accentColor ?: MossGreen).copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = (skin?.accentColor ?: MossGreen).copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.yutorikaizen.R.drawable.ic_ribi_head),
                    contentDescription = "Ribi",
                    modifier = Modifier.size(46.dp).clip(CircleShape)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Ribi & You",
                style = MaterialTheme.typography.titleLarge,
                color = CreamWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Day $dayNumber · Phase $phaseNumber · $difficultyLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = SoftTan
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // NCI Ring
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(54.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = CozyBlack4,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                            val color = when {
                                nciScore >= 0.8f -> NciHigh
                                nciScore >= 0.5f -> NciMid
                                else -> NciLow
                            }
                            drawArc(
                                color = color,
                                startAngle = -90f,
                                sweepAngle = 360f * nciScore,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = "${(nciScore * 100).roundToInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = CreamWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("NCI", style = MaterialTheme.typography.labelSmall, color = SoftTan)
                }

                // Streak
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(54.dp)
                            .background(CozyBlack4, CircleShape)
                    ) {
                        Text(
                            text = "$currentStreak d",
                            style = MaterialTheme.typography.titleMedium,
                            color = CozyAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Streak", style = MaterialTheme.typography.labelSmall, color = SoftTan)
                }
            }
        }
    }
}

@Composable
private fun FeatureNavCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    accentColor: Color = MossGreen,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack2),
        border = BorderStroke(1.dp, CozyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        color = CreamWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (badge != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = accentColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = badge,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftTan
                )
            }
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = SoftTan,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    usageTrackingEnabled: Boolean,
    onUsageTrackingToggle: (Boolean) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
    catOverlayActive: Boolean,
    onCatOverlayToggle: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Preferences",
            style = MaterialTheme.typography.titleSmall,
            color = SoftTan,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CozyBlack2),
            border = BorderStroke(1.dp, CozyBorder)
        ) {
            Column {
                SettingRow(
                    title = "App Usage Tracking",
                    subtitle = "Track screen time & social media usage",
                    icon = Icons.Rounded.Timeline,
                    checked = usageTrackingEnabled,
                    onCheckedChange = onUsageTrackingToggle
                )
                Divider(color = CozyBlack3, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingRow(
                    title = "Notifications",
                    subtitle = "Daily reminders & habit check-in nudges",
                    icon = Icons.Rounded.Notifications,
                    checked = notificationsEnabled,
                    onCheckedChange = onNotificationsToggle
                )
                Divider(color = CozyBlack3, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                SettingRow(
                    title = "🐱 Cat Overlay",
                    subtitle = "Floating cat with quick shortcuts over all apps",
                    icon = Icons.Rounded.Widgets,
                    checked = catOverlayActive,
                    onCheckedChange = onCatOverlayToggle
                )
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MossGreen, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) CreamWhite else DimTan,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SoftTan
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CozyBlack,
                checkedTrackColor = MossGreen,
                uncheckedThumbColor = SoftTan,
                uncheckedTrackColor = CozyBlack4
            )
        )
    }
}

@Composable
private fun DangerZoneSection(
    onResetJourneyClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Journey & Data Controls",
            style = MaterialTheme.typography.titleSmall,
            color = CozyAmber,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedButton(
            onClick = onResetJourneyClick,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, CozyAmber.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = CozyBlack2, contentColor = CozyAmber)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Refresh, contentDescription = null, tint = CozyAmber, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Reset Journey to Day 1", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("Restarts your phase progression from Day 1", style = MaterialTheme.typography.labelSmall, color = SoftTan)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onDeleteClick,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, SurgeRed.copy(alpha = 0.4f)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = CozyBlack2, contentColor = SurgeRed)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = null, tint = SurgeRed, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Reset All Progress & Data", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("Deletes check-ins, habits, and reset to day 1", style = MaterialTheme.typography.labelSmall, color = SoftTan)
                }
            }
        }
    }
}
