package com.freela.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.freela.app.ui.theme.Freela

enum class BigNumberTone { Default, Danger, Success, Warning }

@Composable
fun BigNumber(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    sublabel: String? = null,
    tone: BigNumberTone = BigNumberTone.Default,
) {
    val tokens = Freela.tokens
    val valueColor: Color = when (tone) {
        BigNumberTone.Default -> tokens.ink
        BigNumberTone.Danger -> tokens.danger
        BigNumberTone.Success -> tokens.success
        BigNumberTone.Warning -> tokens.warning
    }
    Column(modifier = modifier.padding(PaddingValues(0.dp))) {
        Text(
            text = label.uppercase(),
            color = tokens.muted,
            style = tokens.typeExtras.monoCap,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = value,
            color = valueColor,
            style = tokens.typeExtras.numberStat,
        )
        if (sublabel != null) {
            Text(
                text = sublabel,
                color = tokens.muted,
                style = tokens.typeExtras.monoMeta,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
