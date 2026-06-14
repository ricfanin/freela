package com.freela.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.freela.app.ui.theme.Freela

@Composable
fun FreelaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: Dp = 16.dp,
    cornerRadius: Dp = 18.dp,
    elevated: Boolean = true,
    background: Color = Freela.tokens.surface,
    content: @Composable () -> Unit,
) {
    FreelaCard(
        modifier = modifier,
        onClick = onClick,
        padding = PaddingValues(padding),
        cornerRadius = cornerRadius,
        elevated = elevated,
        background = background,
        content = content,
    )
}

@Composable
fun FreelaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    padding: PaddingValues,
    cornerRadius: Dp = 18.dp,
    elevated: Boolean = true,
    background: Color = Freela.tokens.surface,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(cornerRadius)
    // column e non box, se no i figli (label, barre, righe) si sovrappongono invece di impilarsi
    Column(
        modifier = modifier
            .then(if (elevated) Modifier.shadow(elevation = 4.dp, shape = shape, clip = false) else Modifier)
            .clip(shape)
            .background(background)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(padding),
    ) {
        content()
    }
}
