package com.example.yutorikaizen.service

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.yutorikaizen.MainActivity
import com.example.yutorikaizen.R
import com.example.yutorikaizen.presentation.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * CatQuickMenuActivity — FooView-inspired Radial Dial Shortcut Menu.
 *
 * Design:
 *  - Center Hub: Animated laptop cat (logocat.json) typing in the middle of the screen
 *  - Radial Ring: 8 circular buttons blossoming outward from the center
 *  - On dismiss or navigate: Returns back to the side overlay as scroll.json
 */
class CatQuickMenuActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(android.R.color.transparent)

        setContent {
            YutoriKaizenTheme {
                RadialFooViewMenu(
                    onNavigate = { destination ->
                        openYutoriKaizen(destination)
                    },
                    onDismiss = { finish() }
                )
            }
        }
    }

    private fun openYutoriKaizen(destination: String) {
        // Immediate in-memory state dispatch
        MainActivity.activeIntercept.value = null
        MainActivity.pendingNavigation.value = destination

        // Notify overlay service to restore the side cat as scroll
        CatOverlayService.notifyPopupClosed()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            )
            putExtra(MainActivity.EXTRA_NAVIGATE_TO, destination)
        }
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        CatOverlayService.notifyPopupClosed()
    }
}

data class FooViewShortcut(
    val label: String,
    val icon: ImageVector,
    val destination: String,
    val color: Color
)

private val FOOVIEW_SHORTCUTS = listOf(
    FooViewShortcut("Home",       Icons.Rounded.Home,           "home",           MossGreen),
    FooViewShortcut("Focus",      Icons.Rounded.Timer,          "focus",          Phase2Blue),
    FooViewShortcut("Habits",     Icons.Rounded.Checklist,      "habits",         SoftSage),
    FooViewShortcut("Goal",       Icons.Rounded.WbSunny,        "daily_goal",     CozyAmber),
    FooViewShortcut("Journey",    Icons.Rounded.Favorite,       "journey",        SurgeRed),
    FooViewShortcut("Expenses",   Icons.Rounded.AttachMoney,    "expenses",       Phase3Amber),
    FooViewShortcut("Insights",   Icons.Rounded.BarChart,       "analytics",      Phase4Purple),
    FooViewShortcut("Break",      Icons.Rounded.SelfImprovement,"frogbreak",      Color(0xFF5BC0BE)),
)

private const val ORBIT_RADIUS_DP = 126f

@Composable
private fun RadialFooViewMenu(
    onNavigate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(20)
        expanded = true
    }

    val handleDismiss: () -> Unit = {
        expanded = false
        onDismiss()
    }

    // Scrim background
    val scrimAlpha by animateFloatAsState(
        targetValue = if (expanded) 0.65f else 0f,
        animationSpec = tween(180),
        label = "scrimAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = scrimAlpha))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = handleDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outer subtle decorative halo
        val haloScale by animateFloatAsState(
            targetValue = if (expanded) 1f else 0.2f,
            animationSpec = spring(
                dampingRatio = 0.7f,
                stiffness = Spring.StiffnessLow
            ),
            label = "haloScale"
        )
        Box(
            modifier = Modifier
                .size(316.dp)
                .scale(haloScale)
                .border(
                    width = 1.dp,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MossGreen.copy(alpha = 0.15f),
                            CozyBorder.copy(alpha = 0.30f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // 8 Radial Blossom Buttons
        FOOVIEW_SHORTCUTS.forEachIndexed { index, shortcut ->
            val angleDeg = -90.0 + (index * (360.0 / FOOVIEW_SHORTCUTS.size))
            val angleRad = Math.toRadians(angleDeg)

            val animProgress by animateFloatAsState(
                targetValue = if (expanded) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = 0.60f,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "progress_$index"
            )

            val currentRadius = ORBIT_RADIUS_DP * animProgress
            val offsetX = (currentRadius * cos(angleRad)).dp
            val offsetY = (currentRadius * sin(angleRad)).dp

            Box(
                modifier = Modifier
                    .offset(x = offsetX, y = offsetY)
                    .scale(animProgress)
                    .alpha(animProgress.coerceIn(0f, 1f)),
                contentAlignment = Alignment.Center
            ) {
                RadialBubbleItem(
                    shortcut = shortcut,
                    onClick = { onNavigate(shortcut.destination) }
                )
            }
        }

        // Center Hub: logocat (cat on laptop) in the middle!
        val centerScale by animateFloatAsState(
            targetValue = if (expanded) 1f else 0.4f,
            animationSpec = spring(
                dampingRatio = 0.65f,
                stiffness = Spring.StiffnessMedium
            ),
            label = "centerScale"
        )

        CenterHubLogocat(
            modifier = Modifier.scale(centerScale),
            onClick = handleDismiss
        )
    }
}

/**
 * Circular FooView-style action bubble.
 */
@Composable
private fun RadialBubbleItem(
    shortcut: FooViewShortcut,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = Color(0xFF1E2023),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .size(54.dp)
                .shadow(elevation = 10.dp, shape = CircleShape, ambientColor = shortcut.color, spotColor = shortcut.color)
                .border(
                    width = 1.5.dp,
                    color = shortcut.color.copy(alpha = 0.85f),
                    shape = CircleShape
                )
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                shortcut.color.copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                Icon(
                    imageVector = shortcut.icon,
                    contentDescription = shortcut.label,
                    tint = shortcut.color,
                    modifier = Modifier.size(25.dp)
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Mini pill label badge
        Surface(
            color = CozyBlack.copy(alpha = 0.90f),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(0.6.dp, CozyBorder)
        ) {
            Text(
                text = shortcut.label,
                color = CreamWhite,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

/**
 * Center Anchor Hub with logocat (cat on laptop) animated in the center!
 * Tapping it closes the menu.
 */
@Composable
private fun CenterHubLogocat(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logocat))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = CozyBlack3,
        shadowElevation = 14.dp,
        modifier = modifier
            .size(76.dp)
            .border(
                width = 2.dp,
                brush = Brush.sweepGradient(
                    colors = listOf(
                        MossGreen,
                        CozyAmber,
                        Phase4Purple,
                        Phase2Blue,
                        MossGreen
                    )
                ),
                shape = CircleShape
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF262A2E),
                            CozyBlack2
                        )
                    )
                )
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(66.dp)
            )
        }
    }
}
