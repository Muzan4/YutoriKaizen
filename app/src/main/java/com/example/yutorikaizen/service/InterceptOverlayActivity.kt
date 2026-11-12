package com.example.yutorikaizen.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yutorikaizen.R
import com.example.yutorikaizen.domain.model.ALL_SKINS
import com.example.yutorikaizen.domain.model.FrogSkin
import com.example.yutorikaizen.presentation.theme.Caveat
import com.example.yutorikaizen.presentation.theme.CozyBlack
import com.example.yutorikaizen.presentation.theme.CozyBorder
import com.example.yutorikaizen.presentation.theme.CreamWhite
import com.example.yutorikaizen.presentation.theme.DimTan
import com.example.yutorikaizen.presentation.theme.DmSans
import com.example.yutorikaizen.presentation.theme.Fraunces
import com.example.yutorikaizen.presentation.theme.MossGlow
import com.example.yutorikaizen.presentation.theme.MossGreen
import com.example.yutorikaizen.presentation.theme.MossGreenDim
import com.example.yutorikaizen.presentation.theme.SoftTan
import com.example.yutorikaizen.presentation.theme.YutoriKaizenTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * InterceptOverlayActivity — the "one sec" pause screen.
 *
 * Displays a fullscreen cozy pause screen where the user's selected frog
 * (Ribi) is actively jumping around with squash-and-stretch physics,
 * matching the home screen's energy and the user's equipped skin.
 */
class InterceptOverlayActivity : ComponentActivity() {

    companion object {
        const val EXTRA_PACKAGE = "pkg"
        const val EXTRA_LABEL   = "label"
        const val EXTRA_PROMPT  = "prompt"
        const val EXTRA_DELAY   = "delay"
        const val EXTRA_SKIN_ID = "skin_id"

        fun resolveSkin(context: Context): FrogSkin {
            return try {
                val skinId = com.example.yutorikaizen.data.local.SkinPreferences(context)
                    .getSelectedSkinIdBlocking()
                ALL_SKINS.find { it.id == skinId } ?: ALL_SKINS.first()
            } catch (_: Exception) {
                ALL_SKINS.first()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appLabel     = intent.getStringExtra(EXTRA_LABEL)  ?: "this app"
        val prompt       = intent.getStringExtra(EXTRA_PROMPT) ?: "Pause for a moment."
        val delaySeconds = intent.getIntExtra(EXTRA_DELAY, 5)
        val skinId       = intent.getStringExtra(EXTRA_SKIN_ID)
        val selectedSkin = ALL_SKINS.find { it.id == skinId } ?: resolveSkin(this)

        setContent {
            YutoriKaizenTheme {
                InterceptOverlayScreen(
                    appLabel     = appLabel,
                    prompt       = prompt,
                    delaySeconds = delaySeconds,
                    skin         = selectedSkin,
                    onProceed    = { finish() },
                    onGoBack     = {
                        startActivity(
                            Intent(Intent.ACTION_MAIN).apply {
                                addCategory(Intent.CATEGORY_HOME)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                        finish()
                    }
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        startActivity(
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
        finish()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen Composable
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun InterceptOverlayScreen(
    appLabel: String,
    prompt: String,
    delaySeconds: Int,
    skin: FrogSkin,
    onProceed: () -> Unit,
    onGoBack: () -> Unit
) {
    var secondsLeft   by remember { mutableIntStateOf(delaySeconds) }
    var countdownDone by remember { mutableStateOf(false) }

    // Countdown tick
    LaunchedEffect(Unit) {
        repeat(delaySeconds) {
            delay(1_000L)
            secondsLeft--
        }
        countdownDone = true
    }

    // Arc ring sweep progress
    val sweepProgress by animateFloatAsState(
        targetValue   = if (countdownDone) 1f
                        else (delaySeconds - secondsLeft).toFloat() / delaySeconds,
        animationSpec = tween(durationMillis = 1_000, easing = LinearEasing),
        label         = "sweep"
    )

    // Breathing glow
    val breathScale by rememberInfiniteTransition(label = "breathe").animateFloat(
        initialValue  = 0.94f,
        targetValue   = 1.04f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0C130D),
                        CozyBlack,
                        Color(0xFF080D09)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {

            // ── App Header ────────────────────────────────────────────────
            Surface(
                color = CozyBlack.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CozyBorder),
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⏳", fontSize = 13.sp)
                    Text(
                        text = "Yutori kaizen · $appLabel",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = DmSans,
                            fontSize = 11.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = skin.accentColor
                    )
                }
            }

            // ── The Frog Arena (Frog Jumping Around Inside Countdown Ring) ──
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier.size(240.dp)
            ) {
                // Background Glow
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(breathScale)
                        .clip(CircleShape)
                        .background(skin.accentColor.copy(alpha = 0.18f))
                )

                // Countdown Track & Progress Ring
                Canvas(modifier = Modifier.size(230.dp)) {
                    val stroke = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    // Track
                    drawArc(
                        color      = CozyBorder,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter  = false,
                        style      = stroke
                    )
                    // Progress arc
                    drawArc(
                        color      = skin.accentColor,
                        startAngle = -90f,
                        sweepAngle = sweepProgress * 360f,
                        useCenter  = false,
                        style      = stroke
                    )
                }

                // Ground shadow beneath the jumping frog
                Canvas(
                    modifier = Modifier
                        .size(width = 110.dp, height = 24.dp)
                        .align(Alignment.BottomCenter)
                        .offset(y = (-32).dp)
                ) {
                    drawOval(
                        color = Color.Black.copy(alpha = 0.45f),
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height)
                    )
                }

                // 🐸 Active Jumping Frog (Using Ribi's pixel-art frog character + equipped skin)
                JumpingRibiFrog(
                    skin = skin,
                    isDone = countdownDone
                )
            }

            Spacer(Modifier.height(18.dp))

            // ── Countdown or Ready Badge ──────────────────────────────────
            AnimatedContent(
                targetState = countdownDone,
                transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(200)) },
                label = "countdownBadge"
            ) { done ->
                if (done) {
                    Surface(
                        color = MossGreenDim,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MossGreen)
                    ) {
                        Text(
                            text = "took a breather? now use your brain",
                            style = MaterialTheme.typography.titleMedium,
                            color = MossGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$secondsLeft",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontFamily = Fraunces,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = CreamWhite
                        )
                        Text(
                            text = "take a deep breath…",
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = Caveat, fontSize = 16.sp),
                            color = SoftTan
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Personal prompt ───────────────────────────────────────────
            Surface(
                color = CozyBlack.copy(alpha = 0.7f),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CozyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"$prompt\"",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = Caveat,
                        fontSize = 24.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = CreamWhite,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }

            Spacer(Modifier.height(36.dp))

            // ── Buttons ───────────────────────────────────────────────────
            AnimatedVisibility(
                visible = countdownDone,
                enter   = fadeIn(tween(400)) + slideInVertically { it / 2 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick  = onProceed,
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = skin.accentColor,
                            contentColor   = CozyBlack
                        ),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text(
                            "Open $appLabel Anyway",
                            style      = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick  = onGoBack,
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.outlinedButtonColors(contentColor = SoftTan),
                        border   = androidx.compose.foundation.BorderStroke(1.dp, CozyBorder),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("← Close & Stay Mindful", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            AnimatedVisibility(
                visible = !countdownDone,
                exit    = fadeOut(tween(300))
            ) {
                Text(
                    text  = "Ribi is giving you space to choose consciously 🐸",
                    style = MaterialTheme.typography.bodySmall,
                    color = DimTan
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// JumpingRibiFrog — The animated frog jumping around on screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun JumpingRibiFrog(
    skin: FrogSkin,
    isDone: Boolean
) {
    // Parabolic vertical jump offset
    val jumpY = remember { Animatable(0f) }
    // Horizontal sway / jump offset
    val jumpX = remember { Animatable(0f) }
    // Squash & Stretch
    val scaleX = remember { Animatable(1f) }
    val scaleY = remember { Animatable(1f) }

    LaunchedEffect(isDone) {
        if (!isDone) {
            var jumpDirection = 1f
            while (true) {
                // 1. Squash before leaping
                launch { scaleY.animateTo(0.72f, tween(100, easing = EaseInOutQuad)) }
                launch { scaleX.animateTo(1.28f, tween(100, easing = EaseInOutQuad)) }
                delay(100)

                // 2. Launch / Rise phase (leaps up and slightly sideways)
                val targetX = jumpDirection * 24f
                launch { scaleY.animateTo(1.24f, tween(240, easing = EaseInOutQuad)) }
                launch { scaleX.animateTo(0.85f, tween(240, easing = EaseInOutQuad)) }
                launch { jumpX.animateTo(targetX, tween(480, easing = FastOutSlowInEasing)) }
                jumpY.animateTo(-64f, tween(240, easing = EaseInOutQuad))

                // 3. Fall & Landing phase (springy bounce on impact)
                launch { scaleY.animateTo(0.70f, tween(220, easing = EaseOutBounce)) }
                launch { scaleX.animateTo(1.30f, tween(220, easing = EaseOutBounce)) }
                jumpY.animateTo(0f, tween(240, easing = EaseOutBounce))

                // 4. Return to natural posture
                launch { scaleY.animateTo(1f, tween(140, easing = FastOutSlowInEasing)) }
                launch { scaleX.animateTo(1f, tween(140, easing = FastOutSlowInEasing)) }

                // Reverse direction for next hop
                jumpDirection *= -1f

                // Pause before next hop
                delay(600L)
            }
        } else {
            // Settled happy idle bob
            launch { scaleY.animateTo(1f, tween(200)) }
            launch { scaleX.animateTo(1f, tween(200)) }
            launch { jumpX.animateTo(0f, tween(250)) }
            jumpY.animateTo(0f, tween(250, easing = FastOutSlowInEasing))
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset(x = jumpX.value.dp, y = jumpY.value.dp)
            .scale(scaleX = scaleX.value, scaleY = scaleY.value)
    ) {
        // Frog Character Card / Sprite Container
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(skin.accentColor.copy(alpha = 0.22f))
                .border(2.dp, skin.accentColor.copy(alpha = 0.8f), CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_ribi_head),
                contentDescription = "Ribi the Frog",
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        // Skin Emoji Accessory Badge
        if (skin.emoji != "🐸") {
            Surface(
                color = CozyBlack.copy(alpha = 0.85f),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, skin.accentColor),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp)
            ) {
                Text(
                    text = skin.emoji,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}
