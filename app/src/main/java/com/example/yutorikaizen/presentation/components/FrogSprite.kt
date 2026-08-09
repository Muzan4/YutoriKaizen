package com.example.yutorikaizen.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.yutorikaizen.domain.model.FrogState
import com.example.yutorikaizen.presentation.theme.*

@Composable
fun FrogSprite(
    frogState: FrogState,
    modifier: Modifier = Modifier,
    isWorking: Boolean = false,
    isBreathing: Boolean = false,
    size: Dp = 120.dp
) {
    // Simple procedural Canvas drawing for a frog placeholder
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f

        // Body (green oval)
        drawOval(
            color = Color(0xFF4CAF50),
            topLeft = Offset(cx - w * 0.35f, cy - h * 0.25f),
            size = Size(w * 0.70f, h * 0.50f)
        )

        // Eyes (two smaller green circles on top)
        drawCircle(
            color = Color(0xFF4CAF50),
            radius = w * 0.12f,
            center = Offset(cx - w * 0.18f, cy - h * 0.25f)
        )
        drawCircle(
            color = Color(0xFF4CAF50),
            radius = w * 0.12f,
            center = Offset(cx + w * 0.18f, cy - h * 0.25f)
        )

        // Eye pupils
        drawCircle(
            color = Color.White,
            radius = w * 0.06f,
            center = Offset(cx - w * 0.18f, cy - h * 0.25f)
        )
        drawCircle(
            color = Color.White,
            radius = w * 0.06f,
            center = Offset(cx + w * 0.18f, cy - h * 0.25f)
        )
        drawCircle(
            color = Color.Black,
            radius = w * 0.03f,
            center = Offset(cx - w * 0.18f, cy - h * 0.25f)
        )
        drawCircle(
            color = Color.Black,
            radius = w * 0.03f,
            center = Offset(cx + w * 0.18f, cy - h * 0.25f)
        )

        // Smile
        drawArc(
            color = Color(0xFF388E3C),
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - w * 0.15f, cy - h * 0.05f),
            size = Size(w * 0.30f, h * 0.20f)
        )
    }
}
