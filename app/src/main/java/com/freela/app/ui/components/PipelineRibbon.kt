package com.freela.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.freela.app.domain.model.FasePipeline
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.stageColor

/**
 * Ribbon orizzontale 10 segmenti che rappresenta la pipeline. Tutti i segmenti fino allo stage
 * corrente sono pieni nel colore della fase, gli altri sono linesoft.
 * Riferimento: design_handoff_freela/screens-core.jsx:441-449
 */
@Composable
fun PipelineRibbon(
    currentStage: FasePipeline,
    modifier: Modifier = Modifier,
) {
    val tokens = Freela.tokens
    val activeColor: Color = stageColor(currentStage)
    val stages = FasePipeline.ordered
    val currentIdx = stages.indexOf(currentStage)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        stages.forEachIndexed { i, _ ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (i <= currentIdx) activeColor else tokens.lineSoft),
            )
        }
    }
}
