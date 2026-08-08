package com.example.yutorikaizen.presentation.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.yutorikaizen.domain.model.DuckState
import com.example.yutorikaizen.presentation.theme.CalmTeal
import com.example.yutorikaizen.presentation.theme.DuckAmber
import com.example.yutorikaizen.presentation.theme.DuckAmberDark
import com.example.yutorikaizen.presentation.theme.StarWhite

/**
 * Public API — same signature whether backed by Canvas or real PNG frames.
 *
 * When you're ready for real pixel art (Phase 5):
 *  1. Create your PNG frames in Aseprite and copy them to res/drawable/
 *  2. Replace the `PlaceholderDuck(...)` call below with:
 *         val tick by produceState(0) { while(true) { delay(frameMs); value++ } }
 *         Image(painterResource(frames(duckState, isWorking, isBreathing)[tick % size]),
 *               contentDescription = "Duck", modifier = modifier.size(size))
 *  3. Every screen using DuckSprite automatically gets real art — zero other changes.
 */
@Composable
fun DuckSprite(
    duckState: DuckState,
    modifier: Modifier = Modifier,
    isWorking: Boolean = false,
    isBreathing: Boolean = false,
    size: Dp = 120.dp
) {
    PlaceholderDuck(
        duckState = duckState,
        isWorking = isWorking,
        isBreathing = isBreathing,
        modifier = modifier.size(size)
    )
}

// ---------------------------------------------------------------------------
// Canvas placeholder — draws the duck using pure shapes, zero image files
// ---------------------------------------------------------------------------
@Composable
private fun PlaceholderDuck(
    duckState: DuckState,
    isWorking: Boolean,
    isBreathing: Boolean,
    modifier: Modifier = Modifier
) {
    // Bob speed: HAPPY bobs fast, SLEEPY barely moves
    val bobPeriodMs = when {
        isBreathing                    -> 4000   // matches box-breathing pace
        duckState == DuckState.HAPPY   -> 600
        duckState == DuckState.CONTENT -> 1200
        else                           -> 2400   // SLEEPY — very slow
    }

    val infiniteTransition = rememberInfiniteTransition(label = "duck_bob")

    // Vertical bob — goes up and comes back down smoothly
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = -1f,
        animationSpec = infiniteRepeatable(
            animation   = tween(bobPeriodMs / 2, easing = EaseInOutSine),
            repeatMode  = RepeatMode.Reverse
        ),
        label = "bob_offset"
    )

    // Amplitude of the bob movement per state
    val amplitude = when {
        isBreathing                    -> 6f
        duckState == DuckState.HAPPY   -> 10f
        duckState == DuckState.CONTENT -> 5f
        else                           -> 2f    // SLEEPY — tiny
    }

    // Eye openness — SLEEPY slowly half-closes its eyes
    val eyeOpenness by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = if (duckState == DuckState.SLEEPY) 0.25f else 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(bobPeriodMs, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eye_openness"
    )

    // Body colour — SLEEPY is slightly muted amber
    val bodyColor = when (duckState) {
        DuckState.HAPPY,
        DuckState.CONTENT -> DuckAmber
        DuckState.SLEEPY  -> DuckAmber.copy(alpha = 0.72f)
    }

    Canvas(modifier = modifier) {
        val w      = size.width
        val h      = size.height
        val yShift = bobOffset * amplitude

        drawPlaceholderDuck(
            w                 = w,
            h                 = h,
            yShift            = yShift,
            bodyColor         = bodyColor,
            eyeOpenness       = eyeOpenness,
            showWorkingPencil = isWorking
        )
    }
}

/**
 * All shape drawing happens here, separated from animation logic.
 *
 * Proportions use fractions of the Canvas size (w/h) so the duck scales
 * correctly regardless of the `size` Dp value passed by the caller.
 */
private fun DrawScope.drawPlaceholderDuck(
    w: Float,
    h: Float,
    yShift: Float,
    bodyColor: Color,
    eyeOpenness: Float,
    showWorkingPencil: Boolean
) {
    val cx = w / 2f
    val cy = h / 2f + yShift

    // Body — large rounded oval
    drawOval(
        color   = bodyColor,
        topLeft = Offset(cx - w * 0.30f, cy - h * 0.20f),
        size    = Size(w * 0.60f, h * 0.48f)
    )

    // Head — smaller circle sitting on top-right of body
    drawCircle(
        color  = bodyColor,
        radius = w * 0.22f,
        center = Offset(cx + w * 0.04f, cy - h * 0.25f)
    )

    // Bill — flat darker oval on the right side of the head
    drawOval(
        color   = DuckAmberDark,
        topLeft = Offset(cx + w * 0.20f, cy - h * 0.30f),
        size    = Size(w * 0.15f, h * 0.08f)
    )

    // Eye — dark circle whose vertical size is driven by eyeOpenness
    // (gives the blinking/sleepy effect without separate frames)
    val eyeRx = w * 0.032f
    val eyeRy = eyeRx * eyeOpenness.coerceAtLeast(0.15f)
    drawOval(
        color   = StarWhite.copy(alpha = 0.15f),   // white highlight ring
        topLeft = Offset(cx + w * 0.14f - eyeRx * 1.4f, cy - h * 0.29f - eyeRy * 1.4f),
        size    = Size(eyeRx * 2.8f, eyeRy * 2.8f)
    )
    drawOval(
        color   = Color(0xFF1A1020),               // dark pupil
        topLeft = Offset(cx + w * 0.14f - eyeRx, cy - h * 0.29f - eyeRy),
        size    = Size(eyeRx * 2f, eyeRy * 2f)
    )

    // Teal scarf/band — accent colour around the neck area
    drawOval(
        color   = CalmTeal.copy(alpha = 0.65f),
        topLeft = Offset(cx - w * 0.10f, cy - h * 0.09f),
        size    = Size(w * 0.30f, h * 0.07f)
    )

    // Wing nub — small ellipse on the left side of the body
    drawOval(
        color   = bodyColor.copy(alpha = 0.55f),
        topLeft = Offset(cx - w * 0.34f, cy + h * 0.02f),
        size    = Size(w * 0.16f, h * 0.24f)
    )

    // "Working" pencil — appears only during focus timer sessions
    if (showWorkingPencil) {
        // Pencil body
        drawRect(
            color   = DuckAmberDark,
            topLeft = Offset(cx + w * 0.26f, cy - h * 0.18f),
            size    = Size(w * 0.06f, h * 0.22f)
        )
        // Pencil tip
        drawOval(
            color   = Color(0xFF2A1800),
            topLeft = Offset(cx + w * 0.26f, cy + h * 0.04f),
            size    = Size(w * 0.06f, h * 0.05f)
        )
        // Eraser (top of pencil — pink-ish)
        drawOval(
            color   = Color(0xFFE8A0A0),
            topLeft = Offset(cx + w * 0.26f, cy - h * 0.20f),
            size    = Size(w * 0.06f, h * 0.04f)
        )
    }
}
