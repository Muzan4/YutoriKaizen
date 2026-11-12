package com.example.yutorikaizen.presentation.screens.analytics

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.yutorikaizen.domain.model.TriggerLog
import com.example.yutorikaizen.presentation.theme.*
import com.example.yutorikaizen.presentation.components.tourTarget
import com.example.yutorikaizen.domain.model.SocialMediaApps
import com.example.yutorikaizen.domain.model.UsageInsight
import com.example.yutorikaizen.domain.model.ScreenTimeTrend
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.Canvas

private val EMOTION_LABELS = mapOf(
    "BOREDOM"   to "Boredom",
    "ANXIETY"   to "Anxiety",
    "LONELY"    to "Lonely",
    "FATIGUE"   to "Fatigue",
    "AVOIDING"  to "Avoiding",
    "BORED"     to "Boredom",
    "TIRED"     to "Tired",
    "ANXIOUS"   to "Anxiety"
)

@Composable
fun AnalyticsDashboardScreen(
    onBack: () -> Unit = {},
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    BackHandler(onBack = onBack)

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshAnalytics()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CozyBlack)
    ) {
        // ── Header with Back Button ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .background(CozyBlack3, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = CreamWhite
                )
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    "Daily Insight",
                    style = MaterialTheme.typography.titleLarge,
                    color = CreamWhite,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Habits, urges & focus overview",
                    style = MaterialTheme.typography.bodySmall,
                    color = SoftTan
                )
            }
        }

        // ── Tabs ─────────────────────────────────────────────────────────────
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor   = CozyBlack2,
            contentColor     = MossGreen,
            indicator        = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color    = MossGreen,
                    height   = 2.dp
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick  = { selectedTab = 0 },
                text     = { Text("Insights", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick  = { selectedTab = 1 },
                text     = { Text("Urge Log", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        when (selectedTab) {
            0 -> InsightsTab(uiState, viewModel)
            1 -> LogTab(uiState.recentLogs)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Insights Tab
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun InsightsTab(uiState: AnalyticsUiState, viewModel: AnalyticsViewModel) {
    var showNciTooltip by remember { mutableStateOf(false) }

    if (showNciTooltip) {
        AlertDialog(
            onDismissRequest = { showNciTooltip = false },
            title = { Text("Daily Consistency Score") },
            text = { Text("Your daily consistency is calculated from:\n\n• Habits Completed (40%)\n• Urges Resisted vs Yielded (35%)\n• Deep Focus Sessions (25%)\n\nConsistent daily small actions help reset and rewire your attention baseline.") },
            confirmButton = {
                TextButton(onClick = { showNciTooltip = false }) {
                    Text("Got it", color = MossGreen)
                }
            },
            containerColor = CozyBlack2,
            titleContentColor = CreamWhite,
            textContentColor = SoftTan
        )
    }

    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.refreshAnalytics()
            isRefreshing = false
        }
    )

    Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats row
            item {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("${uiState.totalResisted}", "Resisted",  MossGreen, Modifier.weight(1f))
                StatCard("${uiState.totalYielded}",  "Yielded",   SurgeRed,  Modifier.weight(1f))
                StatCard("${uiState.currentStreak}", "Day Streak", CozyAmber, Modifier.weight(1f))
            }
        }

        // NCI score + big ring
        item {
            Box(modifier = Modifier.fillMaxWidth().tourTarget("insight_score_card")) {
                NciScoreCard(nci = uiState.overallNci, onInfoClick = { showNciTooltip = true })
            }
        }

        // NCI Line Chart — always show, with empty state
        item {
            NciHistorySection(nciHistory = uiState.nciHistory)
        }

        // Trigger frequency bar chart
        if (uiState.triggerFrequency.isNotEmpty()) {
            item {
                Text("Top Triggers", style = MaterialTheme.typography.labelLarge,
                    color = SoftTan, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                TriggerBarChart(frequency = uiState.triggerFrequency)
            }
        }

        // Screen Time Insights
        if (uiState.usageInsight != null) {
            item {
                Spacer(Modifier.height(16.dp))
                Text("App Usage Insights", style = MaterialTheme.typography.titleMedium,
                    color = CreamWhite, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
            }

            item {
                ScreenTimeCard(uiState.usageInsight)
            }

            item {
                TopAppsCard(uiState.usageInsight)
            }

            item {
                UsageTrendChart(uiState.usageInsight)
            }

            item {
                RecommendationCard(uiState.usageInsight.recommendation)
            }
        }
    }
    
    PullRefreshIndicator(
        refreshing = isRefreshing,
        state = pullRefreshState,
        modifier = Modifier.align(Alignment.TopCenter),
        contentColor = CozyAmber
    )
}
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge,
                color = color, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = SoftTan, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun NciScoreCard(nci: Float, onInfoClick: () -> Unit) {
    val sweep by animateFloatAsState(
        targetValue   = nci * 360f,
        animationSpec = tween(1500, easing = EaseOutCubic),
        label         = "nci_sweep"
    )
    val color = when {
        nci > 0.7f -> NciHigh
        nci > 0.4f -> NciMid
        else       -> NciLow
    }
    val label = when {
        nci > 0.8f -> "Excellent"
        nci > 0.6f -> "Strong"
        nci > 0.4f -> "Building"
        nci > 0.2f -> "Early Days"
        else       -> "Just Started"
    }

    Card(
        shape  = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.07f)),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier          = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(90.dp)) {
                Canvas(modifier = Modifier.size(90.dp)) {
                    drawArc(CozyBlack3, -90f, 360f, false, style = Stroke(10.dp.toPx(), cap = StrokeCap.Round))
                    drawArc(color, -90f, sweep, false, style = Stroke(10.dp.toPx(), cap = StrokeCap.Round))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${(nci * 100).toInt()}%", fontSize = 20.sp, color = color, fontWeight = FontWeight.Bold)
                    Text("Score", fontSize = 10.sp, color = SoftTan)
                }
            }
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Daily Consistency\nScore", style = MaterialTheme.typography.titleMedium,
                        color = CreamWhite, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onInfoClick) {
                        Icon(Icons.Default.Info, contentDescription = "Score Info", tint = SoftTan)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(label, style = MaterialTheme.typography.bodyMedium, color = color)
                Spacer(Modifier.height(8.dp))
                Text("40% habits · 35% surges · 25% focus",
                    style = MaterialTheme.typography.labelSmall, color = DimTan)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NCI History Section
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NciHistorySection(nciHistory: List<Pair<String, Float>>) {
    Text("14-Day Consistency Trend", style = MaterialTheme.typography.labelLarge,
        color = SoftTan, fontWeight = FontWeight.Medium)
    Spacer(Modifier.height(8.dp))
    
    if (nciHistory.isEmpty()) {
        Card(
            shape  = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = CozyBlack3),
            modifier = Modifier.fillMaxWidth().height(140.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No data available yet", color = DimTan, style = MaterialTheme.typography.bodyMedium)
            }
        }
    } else {
        NciLineChart(history = nciHistory)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// NCI Line Chart (Canvas-drawn, no library)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun NciLineChart(history: List<Pair<String, Float>>) {
    val animProgress by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label         = "chart_anim"
    )

    Card(
        shape  = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack3)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().height(140.dp)) {
                // Y-axis labels
                Column(
                    modifier = Modifier.fillMaxHeight().padding(end = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    Text("1.0", style = MaterialTheme.typography.labelSmall, color = DimTan)
                    Text("0.5", style = MaterialTheme.typography.labelSmall, color = DimTan)
                    Text("0.0", style = MaterialTheme.typography.labelSmall, color = DimTan)
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    val w        = size.width
                    val h        = size.height
                    val padding  = 16.dp.toPx()
                    val drawW    = w - padding * 2
                    val drawH    = h - padding * 2
                    val stepX    = if (history.size > 1) drawW / (history.size - 1) else drawW / 2
                    val points   = history.mapIndexed { i, (_, v) ->
                        Offset(padding + if (history.size > 1) i * stepX else stepX, padding + drawH * (1f - v))
                    }
                    val visible  = (points.size * animProgress).toInt().coerceAtLeast(1)

                    // Grid lines at 0, 50, 100%
                    listOf(0f, 0.5f, 1f).forEach { frac ->
                        drawLine(
                            color       = CozyBorder,
                            start       = Offset(0f, padding + drawH * (1f - frac)),
                            end         = Offset(w, padding + drawH * (1f - frac)),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    if (history.size == 1) {
                        // Single point
                        val pt = points[0]
                        drawCircle(MossGreen, 6.dp.toPx(), pt)
                        drawCircle(CozyBlack, 3.dp.toPx(), pt)
                    } else if (visible >= 2) {
                        // Gradient fill
                        val path = Path().apply {
                            moveTo(points[0].x, h - padding)
                            points.take(visible).forEach { lineTo(it.x, it.y) }
                            lineTo(points[visible - 1].x, h - padding)
                            close()
                        }
                        drawPath(
                            path  = path,
                            brush = Brush.verticalGradient(
                                colors = listOf(MossGreen.copy(alpha = 0.25f), Color.Transparent),
                                startY = 0f, endY = h
                            )
                        )
                        // Line
                        for (i in 0 until visible - 1) {
                            drawLine(MossGreen, points[i], points[i + 1], strokeWidth = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        }
                        // Dots
                        points.take(visible).forEach { pt ->
                            drawCircle(MossGreen, 4.dp.toPx(), pt)
                            drawCircle(CozyBlack, 2.dp.toPx(), pt)
                        }
                    }
                }
            }
            // X labels
            Row(
                modifier              = Modifier.fillMaxWidth().padding(start = 32.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (history.size == 1) {
                    Text(history[0].first, style = MaterialTheme.typography.labelSmall, color = DimTan)
                } else {
                    history.filterIndexed { i, _ -> i % (history.size / 3).coerceAtLeast(1) == 0 || i == history.size - 1 }
                        .forEach { (label, _) ->
                            Text(label, style = MaterialTheme.typography.labelSmall, color = DimTan)
                        }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// App Usage Insights Components
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ScreenTimeCard(insight: UsageInsight) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack3),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Today's Screen Time", style = MaterialTheme.typography.labelMedium, color = SoftTan)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                val hours = insight.totalScreenTimeMinutes / 60
                val mins = insight.totalScreenTimeMinutes % 60
                Text("${hours}h ${mins}m", style = MaterialTheme.typography.headlineLarge, color = CreamWhite, fontWeight = FontWeight.Bold)
                
                Spacer(Modifier.width(12.dp))
                val trendColor = when(insight.screenTimeTrend) {
                    ScreenTimeTrend.UP -> SurgeRed
                    ScreenTimeTrend.DOWN -> MossGreen
                    ScreenTimeTrend.STABLE -> CozyAmber
                }
                val trendText = when(insight.screenTimeTrend) {
                    ScreenTimeTrend.UP -> "↑ Above Avg"
                    ScreenTimeTrend.DOWN -> "↓ Below Avg"
                    ScreenTimeTrend.STABLE -> "− Stable"
                }
                Text(trendText, style = MaterialTheme.typography.labelMedium, color = trendColor, modifier = Modifier.padding(bottom = 6.dp))
            }
            
            Spacer(Modifier.height(16.dp))
            val avgMins = insight.averageScreenTimeMinutes.toFloat().coerceAtLeast(1f)
            val progress = (insight.totalScreenTimeMinutes / avgMins).coerceIn(0f, 1f)
            val barColor = if (insight.totalScreenTimeMinutes > avgMins) SurgeRed else MossGreen
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(CozyBlack4)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(CircleShape)
                        .background(barColor)
                )
            }
            Spacer(Modifier.height(8.dp))
            val avgH = insight.averageScreenTimeMinutes / 60
            val avgM = insight.averageScreenTimeMinutes % 60
            Text("7-day average: ${avgH}h ${avgM}m", style = MaterialTheme.typography.labelSmall, color = DimTan)
        }
    }
}

@Composable
private fun TopAppsCard(insight: UsageInsight) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack3),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Top Distractions", style = MaterialTheme.typography.labelMedium, color = SoftTan)
            Spacer(Modifier.height(12.dp))
            
            val maxUsage = insight.topApps.maxOfOrNull { it.totalForegroundMinutes } ?: 1
            
            insight.topApps.forEach { app ->
                val emoji = SocialMediaApps.getEmoji(app.packageName)
                val appName = app.appLabel // Use the actual saved label!
                val mins = app.totalForegroundMinutes
                val h = mins / 60
                val m = mins % 60
                val timeStr = if (h > 0) "${h}h ${m}m" else "${m}m"
                val fraction = (app.totalForegroundMinutes.toFloat() / maxUsage.toFloat()).coerceIn(0f, 1f)
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(emoji, fontSize = 20.sp, modifier = Modifier.width(32.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(appName, style = MaterialTheme.typography.bodyMedium, color = CreamWhite, maxLines = 1)
                            Text(timeStr, style = MaterialTheme.typography.labelMedium, color = SoftTan)
                        }
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(CozyBlack4)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction)
                                    .clip(CircleShape)
                                    .background(CozyAmber)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UsageTrendChart(insight: UsageInsight) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack3),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("7-Day Trend", style = MaterialTheme.typography.labelMedium, color = SoftTan)
            Spacer(Modifier.height(12.dp))
            
            Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                val maxMins = insight.dailyTrend.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
                val avgMins = insight.averageScreenTimeMinutes
                val w = size.width
                val h = size.height
                val barWidth = (w / (insight.dailyTrend.size * 2f)).coerceAtMost(32.dp.toPx())
                val spacing = (w - (barWidth * insight.dailyTrend.size)) / (insight.dailyTrend.size + 1)
                
                val avgY = h - (h * (avgMins.toFloat() / maxMins.toFloat()).coerceIn(0f, 1f))
                
                // Draw average line
                drawLine(
                    color = CozyBorder,
                    start = Offset(0f, avgY),
                    end = Offset(w, avgY),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
                
                insight.dailyTrend.forEachIndexed { i, (_, mins) ->
                    val barH = h * (mins.toFloat() / maxMins.toFloat())
                    val x = spacing + i * (barWidth + spacing)
                    val y = h - barH
                    val color = if (mins > avgMins) SurgeRed else MossGreen
                    
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, y),
                        size = androidx.compose.ui.geometry.Size(barWidth, barH),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
            
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                insight.dailyTrend.forEach { (label, _) ->
                    Text(label, style = MaterialTheme.typography.labelSmall, color = DimTan)
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(recommendation: String) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack2),
        border = BorderStroke(1.dp, MossGreen.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = CozyAmber, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Text(recommendation, style = MaterialTheme.typography.bodyMedium, color = MossGreen)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Trigger Bar Chart
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TriggerBarChart(frequency: Map<String, Int>) {
    val max = frequency.values.maxOrNull() ?: 1
    val animProgress by animateFloatAsState(1f, tween(1000, easing = EaseOutCubic), label = "bar_anim")

    Card(
        shape  = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack3)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            frequency.entries.forEachIndexed { index, (emotion, count) ->
                val label   = EMOTION_LABELS[emotion] ?: emotion
                val fraction = (count.toFloat() / max) * animProgress
                val barColor = when (index) {
                    0    -> SurgeRed
                    1    -> CozyAmber
                    2    -> Phase2Blue
                    3    -> MossGreen
                    else -> Phase4Purple
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(label, style = MaterialTheme.typography.labelMedium,
                        color = SoftTan, modifier = Modifier.width(88.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(CozyBlack4)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction)
                                .clip(CircleShape)
                                .background(barColor)
                        )
                    }
                    Text("$count", style = MaterialTheme.typography.labelSmall,
                        color = barColor, fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp), textAlign = TextAlign.End)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Log Tab
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun LogTab(logs: List<TriggerLog>) {
    if (logs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.yutorikaizen.R.drawable.ic_clipboard), 
                    contentDescription = "Clipboard", 
                    modifier = Modifier.size(64.dp).scale(1.3f).graphicsLayer { blendMode = BlendMode.Screen }.clip(RoundedCornerShape(8.dp))
                )
                Spacer(Modifier.height(16.dp))
                Text("No urge logs yet", style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = Caveat,
                    fontSize = 24.sp
                ),
                    color = SoftTan, textAlign = TextAlign.Center)
                Text("Use the Surge Bridge to log urges",
                    style = MaterialTheme.typography.bodySmall, color = DimTan)
            }
        }
        return
    }

    LazyColumn(
        contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(logs, key = { it.id }) { log ->
            LogRow(log)
        }
    }
}

@Composable
private fun LogRow(log: TriggerLog) {
    val fmt  = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }
    val time = remember(log.timestamp) { fmt.format(Date(log.timestamp)) }
    val labelName = EMOTION_LABELS[log.emotion.uppercase()] ?: log.emotion

    Card(
        shape  = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CozyBlack3)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    Text(
                        labelName,
                        style      = MaterialTheme.typography.labelLarge,
                        color      = CozyAmber,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(time, style = MaterialTheme.typography.bodySmall, color = SoftTan)
                }
                if (!log.note.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(log.note, style = MaterialTheme.typography.bodySmall,
                        color = WarmCream.copy(alpha = 0.75f), maxLines = 2)
                }
            }
        }
    }
}
