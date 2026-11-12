package com.example.yutorikaizen.presentation.screens.intercept

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yutorikaizen.domain.model.ALL_SKINS
import com.example.yutorikaizen.domain.model.FrogSkin
import com.example.yutorikaizen.presentation.theme.Caveat
import com.example.yutorikaizen.presentation.theme.CozyBlack
import com.example.yutorikaizen.presentation.theme.CozyBorder
import com.example.yutorikaizen.presentation.theme.CreamWhite
import com.example.yutorikaizen.presentation.theme.DimTan
import com.example.yutorikaizen.presentation.theme.DmSans
import com.example.yutorikaizen.presentation.theme.Fraunces
import com.example.yutorikaizen.presentation.theme.MossGreen
import com.example.yutorikaizen.presentation.theme.MossGreenDim
import com.example.yutorikaizen.presentation.theme.SoftTan
import kotlinx.coroutines.delay

/**
 * InterceptOverlayScreen — renders inside MainActivity directly over the Godot frog view!
 * The center area is completely open/transparent so the actual Godot frog jumping around
 * on the home screen is 100% visible and interactive.
 */
@Composable
fun InterceptOverlayScreen(
    appLabel: String,
    prompt: String,
    delaySeconds: Int,
    targetPackage: String,
    onOpenApp: () -> Unit,
    onDontOpen: () -> Unit
) {
    val context = LocalContext.current
    var secondsLeft by remember { mutableIntStateOf(delaySeconds) }
    var countdownDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repeat(delaySeconds) {
            delay(1000L)
            secondsLeft--
        }
        countdownDone = true
    }

    val sweepProgress by animateFloatAsState(
        targetValue = if (countdownDone) 1f else (delaySeconds - secondsLeft).toFloat() / delaySeconds,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "sweep"
    )

    val breathScale by rememberInfiniteTransition(label = "breathe").animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // ── TOP SECTION: Header banner & Personal Prompt ───────────────────
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 40.dp, start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = CozyBlack.copy(alpha = 0.88f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MossGreen.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("⏳", fontSize = 14.sp)
                    Text(
                        text = "OPENING ${appLabel.uppercase()}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = DmSans,
                            fontSize = 11.sp,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MossGreen
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Surface(
                color = CozyBlack.copy(alpha = 0.88f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CozyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "\"$prompt\"",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = Caveat,
                            fontSize = 24.sp,
                            lineHeight = 30.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = CreamWhite,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // ── CENTER RING: Surrounds the real Godot frog on screen ────────────
        // Empty inside so the real jumping Godot frog is visible!
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow circle
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .scale(breathScale)
                    .clip(CircleShape)
                    .background(MossGreen.copy(alpha = 0.08f))
            )

            // Outer progress arc
            Canvas(modifier = Modifier.size(230.dp)) {
                val stroke = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                drawArc(
                    color = CozyBorder.copy(alpha = 0.7f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = stroke
                )
                drawArc(
                    color = MossGreen,
                    startAngle = -90f,
                    sweepAngle = sweepProgress * 360f,
                    useCenter = false,
                    style = stroke
                )
            }
        }

        // ── BOTTOM SECTION: Timer & Action Buttons ──────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 28.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Countdown badge
            Surface(
                color = CozyBlack.copy(alpha = 0.88f),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, if (countdownDone) MossGreen else CozyBorder)
            ) {
                AnimatedContent(
                    targetState = countdownDone,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
                    label = "timerBadge"
                ) { done ->
                    if (done) {
                        Text(
                            text = "✓ Took a breather? now use some brains",
                            style = MaterialTheme.typography.titleSmall,
                            color = MossGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "$secondsLeft",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontFamily = Fraunces,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = CreamWhite
                            )
                            Text(
                                text = "seconds left · take a deep breath",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = Caveat, fontSize = 16.sp),
                                color = SoftTan
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = countdownDone,
                enter = fadeIn(tween(300)) + slideInVertically { it / 2 }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            com.example.yutorikaizen.service.AppInterceptService.tempBypassedPkg = targetPackage
                            val launchIntent = context.packageManager.getLaunchIntentForPackage(targetPackage)
                            if (launchIntent != null) {
                                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(launchIntent)
                            }
                            onOpenApp()
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MossGreen,
                            contentColor = CozyBlack
                        ),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text(
                            "Open $appLabel",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onDontOpen,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = CozyBlack.copy(alpha = 0.88f),
                            contentColor = SoftTan
                        ),
                        border = BorderStroke(1.dp, CozyBorder),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text("← Actually, get me out of here", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            AnimatedVisibility(
                visible = !countdownDone,
                exit = fadeOut(tween(200))
            ) {
                Text(
                    text = "Observe your frog and reflect before opening 🐸",
                    style = MaterialTheme.typography.bodySmall,
                    color = DimTan
                )
            }
        }
    }
}
