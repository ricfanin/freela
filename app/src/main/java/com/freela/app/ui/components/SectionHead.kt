package com.freela.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freela.app.ui.theme.Freela

/**
 * Header di sezione: label mono uppercase + count + action link a destra.
 * Riferimento: design_handoff_freela/ui.jsx:167-184
 */
@Composable
fun SectionHead(
    label: String,
    modifier: Modifier = Modifier,
    count: Int? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    val tokens = Freela.tokens
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 4.dp, end = 4.dp, bottom = 10.dp)),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label.uppercase(),
            color = tokens.muted,
            style = tokens.typeExtras.monoCap,
        )
        if (count != null) {
            Text(
                text = "$count",
                color = tokens.faint,
                style = tokens.typeExtras.monoMeta,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (actionText != null) {
            Text(
                text = actionText,
                color = tokens.accentBase,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                ),
                modifier = if (onActionClick != null) Modifier.clickable { onActionClick() } else Modifier,
            )
        }
    }
}
