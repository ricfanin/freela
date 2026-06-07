package com.freela.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freela.app.ui.theme.Freela

/**
 * Header di schermata: leading (avatar/back) + trailing (icon buttons) + title large + subtitle.
 * Riferimento: design_handoff_freela/ui.jsx:187-210
 */
@Composable
fun ScreenHeader(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    large: Boolean = true,
) {
    val tokens = Freela.tokens
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 22.dp, end = 22.dp, top = if (large) 20.dp else 14.dp, bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier.heightIn(min = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                leading?.invoke()
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                trailing?.invoke()
            }
        }
        if (title != null) {
            Spacer(Modifier.heightIn(min = 14.dp))
            Text(
                text = title,
                color = tokens.ink,
                style = if (large) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 14.dp),
            )
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = tokens.muted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
