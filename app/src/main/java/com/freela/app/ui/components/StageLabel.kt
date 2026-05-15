package com.freela.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.freela.app.domain.model.FasePipeline
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.stageColor

/**
 * Etichetta "fase corrente": dot colorato + label mono uppercase.
 * Riferimento: design_handoff_freela/ui.jsx:260-272
 */
@Composable
fun StageLabel(
    stage: FasePipeline,
    modifier: Modifier = Modifier,
    dotSize: androidx.compose.ui.unit.Dp = 6.dp,
    dotColor: Color = stageColor(stage),
) {
    val tokens = Freela.tokens
    val ctx = LocalContext.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(dotSize).clip(CircleShape).background(dotColor))
        Text(
            text = ctx.getString(stage.shortRes).uppercase(),
            color = tokens.ink,
            style = tokens.typeExtras.monoCap,
        )
    }
}
