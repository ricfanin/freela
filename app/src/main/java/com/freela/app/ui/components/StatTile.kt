package com.freela.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freela.app.ui.theme.Freela

enum class StatTileTone { Accent, Danger, Success, Warning }

/**
 * StatTile Stile C: tinted background + barretta verticale 3.dp + label mono uppercase + valore tabular.
 * Riferimento: design_handoff_freela/screens-core.jsx:571-590
 */
@Composable
fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    sub: String? = null,
    tone: StatTileTone = StatTileTone.Accent,
) {
    val tokens = Freela.tokens
    val toneColor: Color = when (tone) {
        StatTileTone.Accent -> tokens.accentBase
        StatTileTone.Danger -> tokens.danger
        StatTileTone.Success -> tokens.success
        StatTileTone.Warning -> tokens.warning
    }
    // Approssimazione di color-mix(in oklch, toneColor 9%, surface): lerp sRGB con t=0.09
    val bg = lerp(tokens.surface, toneColor, 0.09f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg),
    ) {
        // Barretta verticale a sinistra (70% altezza, opacità 0.85)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight(0.7f)
                .width(3.dp)
                .clip(RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
                .background(toneColor.copy(alpha = 0.85f)),
        )
        Column(
            modifier = Modifier.padding(14.dp),
        ) {
            Text(
                text = label.uppercase(),
                color = toneColor,
                style = tokens.typeExtras.monoCap,
            )
            Box(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = tokens.ink,
                style = tokens.typeExtras.numberStat.copy(fontSize = 24.sp),
            )
            if (sub != null) {
                Box(modifier = Modifier.height(4.dp))
                Text(
                    text = sub,
                    color = tokens.muted,
                    style = tokens.typeExtras.monoMeta,
                )
            }
        }
    }
}
