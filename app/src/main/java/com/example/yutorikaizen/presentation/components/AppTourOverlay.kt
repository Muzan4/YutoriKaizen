package com.example.yutorikaizen.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yutorikaizen.R
import com.example.yutorikaizen.presentation.navigation.NavRoute
import com.example.yutorikaizen.presentation.theme.*

/**
 * Global registry that records the exact pixel bounds of composables marked with .tourTarget(key).
 * Allows the tutorial spotlight to dynamically cut out and highlight UI elements with 100% pixel precision.
 */
object TourTargetRegistry {
    private val _targets = mutableStateMapOf<String, Rect>()
    val targets: Map<String, Rect> get() = _targets

    fun updateTarget(key: String, bounds: Rect) {
        if (bounds.width > 10f && bounds.height > 10f) {
            _targets[key] = bounds
        }
    }

    fun getTarget(key: String): Rect? = _targets[key]
}

/**
 * Modifier extension to register any composable's live root-relative bounding box for the tour overlay.
 */
fun Modifier.tourTarget(key: String): Modifier = this.onGloballyPositioned { coordinates ->
    if (coordinates.isAttached) {
        TourTargetRegistry.updateTarget(key, coordinates.boundsInRoot())
    }
}

data class TourStep(
    val title: String,
    val category: String,
    val description: String,
    val targetRoute: String,
    val targetKey: String,
    val cornerRadiusDp: Float = 20f,
    val pointerLabel: String = ""
)

val APP_TOUR_STEPS = listOf(
    TourStep(
        title = "Meet Ribi & Home",
        category = "YOUR COMPANION",
        description = "Ribi is your interactive companion in this 3D environment. He reacts to your focus sessions, keeps you company during dopamine detox, and marks your consistency.",
        targetRoute = NavRoute.Home.route,
        targetKey = "home_mascot",
        cornerRadiusDp = 28f,
        pointerLabel = "Interactive Mascot"
    ),
    TourStep(
        title = "The Surge Bridge",
        category = "EMERGENCY RESET",
        description = "When intense dopamine urges strike, use this button. Craving neuro-chemicals peak and dissolve within 90 seconds of guided physiological sigh breathing.",
        targetRoute = NavRoute.Home.route,
        targetKey = "home_surge_button",
        cornerRadiusDp = 24f,
        pointerLabel = "90-Second Urge Reset"
    ),
    TourStep(
        title = "Deep Focus & Rest",
        category = "FOCUS & RECOVERY",
        description = "Start structured Pomodoro focus sessions to rebuild attention span, or take screen-free restorative breaks to let your prefrontal cortex recharge.",
        targetRoute = NavRoute.Home.route,
        targetKey = "home_focus_break_row",
        cornerRadiusDp = 18f,
        pointerLabel = "Focus & Rest Actions"
    ),
    TourStep(
        title = "Habit Tracker",
        category = "HABITS",
        description = "Track positive building habits and triggers you want to break. Set custom target frequencies, check them off, and track your streaks over time.",
        targetRoute = NavRoute.Habits.route,
        targetKey = "habits_card",
        cornerRadiusDp = 24f,
        pointerLabel = "Habits & Streaks"
    ),
    TourStep(
        title = "Daily Goal & Contract",
        category = "DAILY INTENTIONS",
        description = "Set your single non-negotiable morning intention, review evening reflections, and maintain anti-trigger If-Then implementation contracts.",
        targetRoute = NavRoute.DailyGoal.route,
        targetKey = "daily_goal_card",
        cornerRadiusDp = 22f,
        pointerLabel = "Daily Intention & Contract"
    ),
    TourStep(
        title = "90-Day Dopamine Journey",
        category = "NEURO-RECOVERY JOURNEY",
        description = "A structured roadmap spanning 4 neural recovery phases: Reset & Detox, Rebuilding, Rewiring, and Mastery, with progressive daily challenges.",
        targetRoute = NavRoute.Journey.route,
        targetKey = "journey_phase_card",
        cornerRadiusDp = 24f,
        pointerLabel = "Phases & Missions"
    ),
    TourStep(
        title = "Daily Insight",
        category = "ANALYTICS",
        description = "View your composite consistency score weighted across habits, resisted surges, and focus minutes, along with craving trigger analytics.",
        targetRoute = NavRoute.Analytics.route,
        targetKey = "insight_score_card",
        cornerRadiusDp = 24f,
        pointerLabel = "Daily Consistency Score"
    ),
    TourStep(
        title = "Settings & Profile",
        category = "PREFERENCES",
        description = "Manage your profile, view streaks, configure background sync, customize appearance, or manage your journey data.",
        targetRoute = NavRoute.Account.route,
        targetKey = "profile_header_card",
        cornerRadiusDp = 24f,
        pointerLabel = "Profile & Controls"
    ),
    TourStep(
        title = "App Pause",
        category = "DISTRACTION INTERCEPT",
        description = "Select doomscrolling apps. A brief, mindful breathing pause will appear before the app opens to interrupt autopilot compulsive checks.",
        targetRoute = NavRoute.InterceptSettings.route,
        targetKey = "app_pause_button",
        cornerRadiusDp = 22f,
        pointerLabel = "App Pause Protection"
    ),
    TourStep(
        title = "Wardrobe & Outfits",
        category = "CUSTOMIZATION",
        description = "Equip different outfits and looks for Ribi in real-time, including Viking, Cowboy, Hat, Clown, Tanp, Green, and Purple styles.",
        targetRoute = NavRoute.Wardrobe.route,
        targetKey = "wardrobe_preview",
        cornerRadiusDp = 24f,
        pointerLabel = "Outfits & Skins"
    ),
    TourStep(
        title = "Impulse Expense Tracker",
        category = "FINANCIAL AWARENESS",
        description = "Log money saved by avoiding cravings versus impulsive purchases, with monthly categorical breakdowns.",
        targetRoute = NavRoute.Expenses.route,
        targetKey = "expense_summary_card",
        cornerRadiusDp = 24f,
        pointerLabel = "Savings vs Mindless Spending"
    ),
    TourStep(
        title = "Ready to Begin",
        category = "GET STARTED",
        description = "You've explored every feature. You can adjust settings or restart your journey at any time from Profile. Let's begin your mindful daily practice.",
        targetRoute = NavRoute.Home.route,
        targetKey = "home_mascot",
        cornerRadiusDp = 28f,
        pointerLabel = "Start Your Journey"
    )
)

@Composable
fun AppTourOverlay(
    currentStep: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSkip: () -> Unit
) {
    // Phone system back button / gesture navigates to previous tutorial step & screen
    BackHandler(enabled = true) {
        if (currentStep > 0) onPrev() else onSkip()
    }

    val stepData = APP_TOUR_STEPS.getOrElse(currentStep) { APP_TOUR_STEPS.first() }
    val registeredBounds = TourTargetRegistry.targets[stepData.targetKey]

    // Animated glowing pulsing border around the highlighted element
    val infiniteTransition = rememberInfiniteTransition(label = "spotlight_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val pulseStroke by infiniteTransition.animateFloat(
        initialValue = 2.dp.value,
        targetValue = 3.5.dp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_stroke"
    )

    // Ensure off-screen compositing layer so BlendMode.Clear cuts through to live app
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(alpha = 0.99f)
            .pointerInput(Unit) {
                detectTapGestures { /* consume tap so underlying screen buttons aren't triggered during tour */ }
            }
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        // Compute exact highlight bounds with graceful fallback
        val rawBounds = if (registeredBounds != null && registeredBounds.width > 20f && registeredBounds.height > 20f) {
            registeredBounds
        } else {
            // Screen-relative fallback if target hasn't reported bounds yet
            when (stepData.targetKey) {
                "home_surge_button" -> Rect(screenWidthPx * 0.05f, screenHeightPx * 0.76f, screenWidthPx * 0.95f, screenHeightPx * 0.84f)
                "home_focus_break_row" -> Rect(screenWidthPx * 0.05f, screenHeightPx * 0.85f, screenWidthPx * 0.95f, screenHeightPx * 0.93f)
                "home_mascot" -> Rect(screenWidthPx * 0.15f, screenHeightPx * 0.28f, screenWidthPx * 0.85f, screenHeightPx * 0.58f)
                else -> Rect(screenWidthPx * 0.06f, screenHeightPx * 0.12f, screenWidthPx * 0.94f, screenHeightPx * 0.38f)
            }
        }

        // Add 8dp padding around the target for a neat, breathing highlight frame
        val padH = 8.dp.value * (screenWidthPx / 360f).coerceIn(1.5f, 3.5f)
        val padV = 6.dp.value * (screenWidthPx / 360f).coerceIn(1.5f, 3.5f)
        val highlightRect = Rect(
            left = (rawBounds.left - padH).coerceAtLeast(0f),
            top = (rawBounds.top - padV).coerceAtLeast(0f),
            right = (rawBounds.right + padH).coerceAtMost(screenWidthPx),
            bottom = (rawBounds.bottom + padV).coerceAtMost(screenHeightPx)
        )

        // Determine if target is in bottom half of screen
        val isTargetInBottomHalf = highlightRect.center.y > (screenHeightPx * 0.5f)

        // ── Canvas Spotlight Cutout ──────────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cutW = (highlightRect.width).coerceAtLeast(20f)
            val cutH = (highlightRect.height).coerceAtLeast(20f)
            val cornerRadius = CornerRadius(stepData.cornerRadiusDp.dp.toPx())

            // 1. Darken entire background
            drawRect(Color.Black.copy(alpha = 0.82f))

            // 2. Punch out the spotlight hole directly over the live composable
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(highlightRect.left, highlightRect.top),
                size = Size(cutW, cutH),
                cornerRadius = cornerRadius,
                blendMode = BlendMode.Clear
            )

            // 3. Draw pulsing glowing border around the cutout
            drawRoundRect(
                color = MossGreen.copy(alpha = pulseAlpha),
                topLeft = Offset(highlightRect.left, highlightRect.top),
                size = Size(cutW, cutH),
                cornerRadius = cornerRadius,
                style = Stroke(width = pulseStroke.dp.toPx())
            )
        }

        // ── Dialog Tooltip Card ──────────────────────────────────────────────
        // If target is in bottom half: place card at TOP so it never covers buttons
        // If target is in top half: place card at BOTTOM so it never covers content
        val alignment = if (isTargetInBottomHalf) Alignment.TopCenter else Alignment.BottomCenter
        val topPad = if (isTargetInBottomHalf) 52.dp else 16.dp
        val bottomPad = if (isTargetInBottomHalf) 16.dp else 36.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = topPad, bottom = bottomPad),
            contentAlignment = alignment
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CozyBlack2)
                    .border(1.5.dp, MossGreen.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header: Category, Step counter, Skip button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = MossGreen.copy(alpha = 0.20f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stepData.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MossGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "${currentStep + 1} / ${APP_TOUR_STEPS.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftTan,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(
                            onClick = onSkip,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                        ) {
                            Text("Skip Tour", color = DimTan, fontSize = 12.sp)
                        }
                    }
                }

                // Spotlight Pointer Pill
                if (stepData.pointerLabel.isNotBlank()) {
                    Surface(
                        color = CozyAmber.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CozyAmber.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = stepData.pointerLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = CozyAmber,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Title and Ribi Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_ribi_head),
                        contentDescription = "Ribi",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .border(1.dp, MossGreen, CircleShape)
                    )
                    Column {
                        Text(
                            text = stepData.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = CreamWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Description
                Text(
                    text = stepData.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftTan,
                    lineHeight = 20.sp
                )

                // Actions: Back and Next / Finish
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = onPrev,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, WarmCream.copy(alpha = 0.2f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmCream)
                        ) {
                            Text("Back", fontSize = 13.sp)
                        }
                    }

                    Button(
                        onClick = onNext,
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MossGreen,
                            contentColor = CozyBlack
                        )
                    ) {
                        Text(
                            if (currentStep < APP_TOUR_STEPS.size - 1) "Next" else "Begin Journey",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
