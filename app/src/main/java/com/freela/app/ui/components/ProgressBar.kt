package com.freela.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.freela.app.ui.theme.Freela

/**
 * Progress bar con supporto overshoot: se value > max, riempie 100% in rosso.
 * Riferimento: design_handoff_freela/ui.jsx:156-164
 */
@Composable
fun FreelaProgressBar(
    value: Float,
    modifier: Modifier = Modifier,
    max: Float = 100f,
    height: Dp = 4.dp,
    color: Color = Freela.tokens.accentBase,
    backgroundColor: Color = Freela.tokens.lineSoft,
    overshoot: Boolean = false,
) {
    val safeMax = if (max > 0f) max else 1f
    val ratio = (value / safeMax).coerceIn(0f, 1f)
    val isOver = overshoot && value > safeMax
    val fillColor = if (isOver) Freela.tokens.danger else color
    val displayRatio = if (isOver) 1f else ratio
    val shape = RoundedCornerShape(percent = 50)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(backgroundColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(displayRatio)
                .fillMaxHeight()
                .clip(shape)
                .background(fillColor),
        )
    }
}
