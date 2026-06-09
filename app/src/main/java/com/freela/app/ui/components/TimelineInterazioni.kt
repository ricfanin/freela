package com.freela.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.freela.app.domain.model.Interazione
import com.freela.app.domain.model.TipoInterazione
import com.freela.app.ui.theme.Freela
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Timeline verticale con bubble icona + data mono + testo. Pin accent se MEETING con location.
 * Riferimento: design_handoff_freela/screens-core.jsx:497-527
 */
@Composable
fun TimelineInterazioni(
    interazioni: List<Interazione>,
    modifier: Modifier = Modifier,
) {
    val tokens = Freela.tokens
    val lineColor = tokens.lineSoft
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                // Linea verticale che connette i bubble (centro bubble 36dp = 18dp).
                val x = 18.dp.toPx()
                val top = 18.dp.toPx()
                val bottom = (size.height - 18.dp.toPx()).coerceAtLeast(top)
                drawLine(
                    color = lineColor,
                    start = Offset(x, top),
                    end = Offset(x, bottom),
                    strokeWidth = 1.dp.toPx(),
                )
            },
    ) {
        interazioni.forEachIndexed { idx, it ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(tokens.surface)
                        .border(width = 1.dp, color = tokens.line, shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    val isMeetingWithLocation = it.tipo == TipoInterazione.MEETING && it.indirizzo != null
                    val tint = if (isMeetingWithLocation) tokens.accentBase else tokens.muted
                    Icon(
                        imageVector = when (it.tipo) {
                            TipoInterazione.MEETING -> Icons.Outlined.LocationOn
                            TipoInterazione.CALL -> Icons.Outlined.Phone
                            TipoInterazione.EMAIL -> Icons.Outlined.AlternateEmail
                            TipoInterazione.MESSAGGIO -> Icons.Outlined.ChatBubbleOutline
                            TipoInterazione.NOTA, TipoInterazione.ALTRO -> Icons.Outlined.Description
                        },
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(15.dp),
                    )
                }
                Column(
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = it.tipo.name.uppercase(),
                            color = tokens.muted,
                            style = tokens.typeExtras.monoCap,
                        )
                        Text(
                            text = formatData(it.data),
                            color = tokens.faint,
                            style = tokens.typeExtras.monoMeta,
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    if (!it.descrizione.isNullOrBlank()) {
                        Text(
                            text = it.descrizione,
                            color = tokens.ink,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    if (!it.indirizzo.isNullOrBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = tokens.accentBase,
                                modifier = Modifier.size(12.dp),
                            )
                            Text(it.indirizzo, color = tokens.accentBase, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

private val timelineDf = SimpleDateFormat("d MMM · HH:mm", Locale.ITALIAN)
private fun formatData(millis: Long): String = timelineDf.format(Date(millis))
