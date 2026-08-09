package com.example.yutorikaizen.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.yutorikaizen.presentation.theme.CalmTeal
import com.example.yutorikaizen.presentation.theme.RingPurple
import com.example.yutorikaizen.presentation.theme.RingTrack

/**
 * Circular consistency ring drawn on a Canvas.
 *
 * WHY custom Canvas instead of CircularProgressIndicator?
 * We need a two-colour gradient sweep (purple → teal) and a glowing track
 * that Material3's built-in component doesn't support without a lot of hacks.
 *
 * The ring animates smoothly via spring() whenever the score changes.
 */
@Composable
fun ConsistencyRing(
    score: Float,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateFloatAsState(
        targetValue    = score.coerceIn(0f, 1f),
        animationSpec  = spring(dampingRatio = 0.7f, stiffness = 50f),
        label          = "consistency_ring_anim"
    )

    Canvas(modifier = modifier) {
        val strokePx = 14.dp.toPx()
        val radius   = (size.minDimension - strokePx) / 2f
        val topLeft  = Offset(center.x - radius, center.y - radius)
        val arcSize  = Size(radius * 2f, radius * 2f)

        // Background track
        drawCircle(
            color  = RingTrack,
            radius = radius,
            style  = Stroke(width = strokePx, cap = StrokeCap.Round)
        )

        // Progress arc — blends purple (low) to teal (high) via score
        val blended = androidx.compose.ui.graphics.lerp(RingPurple, CalmTeal, animatedScore)
        drawArc(
            color      = blended,
            startAngle = -90f,
            sweepAngle = 360f * animatedScore,
            useCenter  = false,
            style      = Stroke(width = strokePx, cap = StrokeCap.Round),
            topLeft    = topLeft,
            size       = arcSize
        )
    }
}
