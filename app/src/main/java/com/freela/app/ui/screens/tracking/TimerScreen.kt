package com.freela.app.ui.screens.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freela.app.R
import com.freela.app.service.TimerForegroundService
import com.freela.app.ui.components.Avatar
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.FreelaProgressBar
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.theme.Freela
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun TimerScreen(
    onBack: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens
    val context = LocalContext.current

    val sessione = state.sessioneAttiva
    val inCorso = sessione != null

    // Cronometro live: tick ogni secondo quando una sessione è attiva.
    val elapsedMillis by produceState(0L, sessione?.inizio) {
        val inizio = sessione?.inizio
        if (inizio == null) {
            value = 0L
        } else {
            while (true) {
                value = System.currentTimeMillis() - inizio
                delay(1_000)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(tokens.bg),
    ) {
        ScreenHeader(
            title = stringResource(R.string.timer_title),
            large = false,
            leading = {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .border(1.dp, tokens.line, CircleShape)
                        .clickable { onBack() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = stringResource(R.string.content_desc_back), tint = tokens.ink, modifier = Modifier.size(18.dp))
                }
            },
            trailing = {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .border(1.dp, tokens.line, CircleShape)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.History, contentDescription = null, tint = tokens.ink, modifier = Modifier.size(16.dp))
                }
            },
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(if (inCorso) tokens.accentBase else tokens.muted))
                Text(
                    text = (if (inCorso) stringResource(R.string.timer_label_running) else stringResource(R.string.timer_label_idle)).uppercase(),
                    color = if (inCorso) tokens.accentBase else tokens.muted,
                    style = tokens.typeExtras.monoCap,
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = formatElapsed(elapsedMillis),
                color = tokens.ink,
                style = tokens.typeExtras.timerHero,
            )
            Spacer(Modifier.height(14.dp))
            if (inCorso && sessione != null) {
                Text(
                    text = stringResource(R.string.timer_subtitle_started, formatOra(sessione.inizio)),
                    color = tokens.muted,
                    style = MaterialTheme.typography.bodySmall,
                )
            } else {
                Text(
                    text = stringResource(R.string.timer_idle_hint),
                    color = tokens.muted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            // Client + activity selector
            Spacer(Modifier.height(22.dp))
            FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val c = state.clienteAttivo
                    if (c != null) {
                        Avatar(name = c.nome, size = 40.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(c.nome, color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                sessione?.descrizione ?: stringResource(R.string.timer_no_activity),
                                color = tokens.muted,
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    } else {
                        Text("Nessun cliente selezionato", color = tokens.muted, modifier = Modifier.weight(1f))
                    }
                    if (c != null) {
                        Icon(
                            Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            tint = tokens.faint,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (inCorso) {
                    CircleAction(Icons.Outlined.Stop, size = 64.dp, isPrimary = true, primaryColor = tokens.danger) {
                        TimerForegroundService.ferma(context)
                    }
                } else {
                    val cliente = state.clienteAttivo
                    CircleAction(Icons.Outlined.PlayArrow, size = 64.dp, isPrimary = true, primaryColor = tokens.accentBase) {
                        if (cliente != null) TimerForegroundService.avvia(context, cliente.id, cliente.nome)
                    }
                }
                val cliente = state.clienteAttivo
                CircleAction(Icons.Outlined.Add, size = 56.dp, isPrimary = false) {
                    // Inserimento manuale rapido di 30 min (PRD FR-19).
                    if (cliente != null) viewModel.aggiungiManuale(cliente.id, 30, null)
                }
            }
        }

        // Progetto
        state.clienteAttivo?.let { c ->
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp)) {
                SectionHead(label = stringResource(R.string.timer_section_progetto))
                FreelaCard(modifier = Modifier.fillMaxWidth(), padding = 18.dp) {
                    val orePrev = c.orePreventivate ?: 0f
                    val oreReali = state.oreRealiMillis / 3_600_000f
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.timer_progress_label), color = tokens.muted, fontSize = 13.sp)
                        Text(
                            "${oreReali.toInt()}h / ${orePrev.toInt()}h",
                            color = tokens.ink,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            style = tokens.typeExtras.monoMeta.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    FreelaProgressBar(value = oreReali, max = orePrev.coerceAtLeast(1f), height = 6.dp)
                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(stringResource(R.string.timer_label_restano).uppercase(), color = tokens.muted, style = tokens.typeExtras.monoCap)
                            Text("${(orePrev - oreReali).toInt().coerceAtLeast(0)}h", color = tokens.ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text(stringResource(R.string.timer_label_ricavo).uppercase(), color = tokens.muted, style = tokens.typeExtras.monoCap)
                            val ricavo = if (oreReali > 0f) ((c.importoPreventivato ?: 0.0) / oreReali).toInt() else 0
                            Text("€$ricavo/h", color = tokens.ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

private fun formatElapsed(millis: Long): String {
    val totalSec = (millis / 1000).coerceAtLeast(0)
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
}

private fun formatOra(millis: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))

@Composable
private fun CircleAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    size: androidx.compose.ui.unit.Dp,
    isPrimary: Boolean,
    primaryColor: Color = Color.Unspecified,
    onClick: () -> Unit,
) {
    val tokens = Freela.tokens
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (isPrimary) primaryColor else Color.Transparent)
            .border(width = if (isPrimary) 0.dp else 1.5.dp, color = tokens.line, shape = CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isPrimary) Color.White else tokens.ink,
            modifier = Modifier.size(size * 0.35f),
        )
    }
}
