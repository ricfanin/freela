package com.freela.app.ui.screens.oggi

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.freela.app.ui.components.FreelaButton
import com.freela.app.ui.components.FreelaButtonSize
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.PillShape
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun OggiScreen(
    onNavigateToCliente: (Long) -> Unit,
    onNavigateToFinanze: () -> Unit,
    onNavigateToStorico: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onStartTimer: () -> Unit,
    viewModel: OggiViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens
    val context = LocalContext.current

    val today = remember {
        SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ITALIAN).format(Date()).replaceFirstChar { it.uppercase() }
    }
    val mese = remember {
        SimpleDateFormat("MMMM yyyy", Locale.ITALIAN).format(Date()).replaceFirstChar { it.uppercase() }
    }
    val fasciaMese = remember {
        when (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
            in 1..10 -> "inizio mese"
            in 11..20 -> "metà mese"
            else -> "fine mese"
        }
    }
    val nome = state.persona?.displayName ?: "—"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.oggi_greeting, nome),
            subtitle = today,
            leading = { Avatar(name = nome, size = 36.dp) },
            trailing = {
                HeaderIcon(Icons.Outlined.Settings, onClick = onNavigateToSettings)
                HeaderIcon(Icons.Outlined.History, onClick = onNavigateToStorico)
            },
        )

        // Card sessione (centrale)
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            if (state.sessione != null) {
                SessioneCard(
                    descrizione = state.sessione?.descrizione,
                    clienteNome = state.clienteAttivo?.nome,
                    inizio = state.sessione?.inizio,
                    onApriCliente = { state.clienteAttivo?.let { onNavigateToCliente(it.id) } },
                    onStop = { TimerForegroundService.ferma(context) },
                    onApriTimer = onStartTimer,
                )
            } else {
                NessunaSessioneCard(onStart = onStartTimer)
            }
        }

        // Riassunto finanziario
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            SectionHead(
                label = stringResource(R.string.oggi_section_riassunto),
                actionText = "Finanze →",
                onActionClick = onNavigateToFinanze,
            )
            RiassuntoCard(
                mese = "$mese · $fasciaMese",
                fatturato = state.fatturato,
                incassato = state.incassato,
                oreMese = state.oreMese,
                numClienti = state.numClienti,
                obiettivo = state.obiettivo,
                periodoIndex = when (state.periodo) {
                    PeriodoOggi.SETTIMANA -> 0
                    PeriodoOggi.MESE -> 1
                    PeriodoOggi.ANNO -> 2
                },
                onPeriodo = { i ->
                    viewModel.selezionaPeriodo(
                        when (i) {
                            0 -> PeriodoOggi.SETTIMANA
                            2 -> PeriodoOggi.ANNO
                            else -> PeriodoOggi.MESE
                        },
                    )
                },
            )
        }

        // Sezioni operative (PRD FR-16)
        if (state.daContattare.isNotEmpty()) {
            SezioneOperativa(
                titolo = stringResource(R.string.oggi_section_contattare),
                voci = state.daContattare,
                onClickVoce = { it.clienteId?.let(onNavigateToCliente) },
            )
        }
        if (state.daConsegnare.isNotEmpty()) {
            SezioneOperativa(
                titolo = stringResource(R.string.oggi_section_consegnare),
                voci = state.daConsegnare,
                onClickVoce = { it.clienteId?.let(onNavigateToCliente) },
            )
        }
        if (state.pagamenti.isNotEmpty()) {
            SezioneOperativa(
                titolo = stringResource(R.string.oggi_section_pagamenti),
                voci = state.pagamenti,
                onClickVoce = { it.clienteId?.let(onNavigateToCliente) },
            )
        }
    }
}

@Composable
private fun SezioneOperativa(
    titolo: String,
    voci: List<OggiVoce>,
    onClickVoce: (OggiVoce) -> Unit,
) {
    val tokens = Freela.tokens
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        SectionHead(label = titolo, count = voci.size)
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(tokens.surface)
                .border(1.dp, tokens.lineSoft, RoundedCornerShape(18.dp)),
        ) {
            voci.forEachIndexed { i, voce ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClickVoce(voce) }
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(voce.titolo, color = tokens.ink, fontSize = 13.5f.sp, fontWeight = FontWeight.SemiBold)
                        Text(voce.sottotitolo, color = tokens.muted, fontSize = 12.sp)
                    }
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = tokens.faint,
                        modifier = Modifier.size(14.dp),
                    )
                }
                if (i < voci.size - 1) {
                    Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                }
            }
        }
    }
}

@Composable
private fun HeaderIcon(icon: ImageVector, onClick: () -> Unit, badge: Boolean = false) {
    val tokens = Freela.tokens
    Box(
        modifier = Modifier.size(36.dp).clip(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tokens.muted, modifier = Modifier.size(20.dp))
        if (badge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(tokens.accentBase)
                    .border(1.5.dp, tokens.bg, CircleShape),
            )
        }
    }
}

@Composable
private fun SessioneCard(
    descrizione: String?,
    clienteNome: String?,
    inizio: Long?,
    onApriCliente: () -> Unit,
    onStop: () -> Unit,
    onApriTimer: () -> Unit,
) {
    val tokens = Freela.tokens
    val elapsed by produceState(0L, inizio) {
        if (inizio == null) value = 0L
        else while (true) { value = System.currentTimeMillis() - inizio; delay(1_000) }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(tokens.surfaceLow)
            .padding(top = 14.dp, bottom = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(tokens.accentBase))
            Text(
                stringResource(R.string.oggi_sessione_in_corso).uppercase(),
                color = tokens.accentBase,
                style = tokens.typeExtras.monoCap,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = formatElapsed(elapsed),
            color = tokens.ink,
            style = tokens.typeExtras.timerHero.copy(fontSize = 56.sp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.oggi_sessione_hint),
            color = tokens.muted,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        // Riga cliente
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(tokens.surface)
                .clickable { onApriCliente() }
                .padding(horizontal = 13.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Avatar(name = clienteNome ?: "—", size = 36.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(descrizione ?: stringResource(R.string.timer_no_activity), color = tokens.ink, fontSize = 13.5f.sp, fontWeight = FontWeight.SemiBold)
                if (clienteNome != null) {
                    Text(clienteNome, color = tokens.muted, fontSize = 12.sp)
                }
            }
            Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = tokens.faint, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.height(12.dp))
        // Controlli
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clip(PillShape)
                    .background(tokens.accentSofter)
                    .clickable { onApriTimer() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                Icon(Icons.Outlined.Pause, contentDescription = null, tint = tokens.ink, modifier = Modifier.size(16.dp))
                Text(stringResource(R.string.oggi_pausa), color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(tokens.danger.copy(alpha = 0.08f))
                    .clickable { onStop() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.Stop, contentDescription = null, tint = tokens.danger, modifier = Modifier.size(16.dp))
            }
            Box(
                modifier = Modifier.size(46.dp).clip(CircleShape).clickable { onApriTimer() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = tokens.muted, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun NessunaSessioneCard(onStart: () -> Unit) {
    val tokens = Freela.tokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(tokens.surfaceLow)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.timer_label_idle), color = tokens.muted, style = tokens.typeExtras.monoCap)
        Text("00:00:00", color = tokens.ink, style = tokens.typeExtras.timerHero.copy(fontSize = 48.sp))
        FreelaButton(
            text = stringResource(R.string.oggi_avvia_timer),
            onClick = onStart,
            size = FreelaButtonSize.Small,
        )
    }
}

@Composable
private fun RiassuntoCard(
    mese: String,
    fatturato: Double,
    incassato: Double,
    oreMese: Float,
    numClienti: Int,
    obiettivo: Double,
    periodoIndex: Int,
    onPeriodo: (Int) -> Unit,
) {
    val tokens = Freela.tokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(tokens.surface)
            .border(1.dp, tokens.lineSoft, RoundedCornerShape(18.dp)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(mese, color = tokens.ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.clip(PillShape).background(tokens.accentSofter).padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                listOf("S", "M", "A").forEachIndexed { i, t ->
                    val on = i == periodoIndex
                    Box(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(if (on) tokens.surface else Color.Transparent)
                            .clickable { onPeriodo(i) }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(t, color = if (on) tokens.ink else tokens.muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        // Stat row
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 6.dp)) {
            StatCol("FATT.", formatK(fatturato), Modifier.weight(1f))
            StatCol("INCASS.", formatK(incassato), Modifier.weight(1f))
            StatCol("ORE", "${oreMese.toInt()}h", Modifier.weight(1f))
            StatCol("CLIENTI", "$numClienti", Modifier.weight(1f))
        }
        // Obiettivo
        val perc = if (obiettivo > 0) (incassato / obiettivo).coerceIn(0.0, 1.0) else 0.0
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "OBIETTIVO ${formatK(obiettivo)}",
                    color = tokens.muted,
                    style = tokens.typeExtras.monoCap,
                )
                Text("${(perc * 100).toInt()}%", color = tokens.ink, fontSize = 11.5f.sp)
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(5.dp).clip(PillShape).background(tokens.lineSoft),
            ) {
                Box(Modifier.fillMaxWidth(perc.toFloat()).height(5.dp).clip(PillShape).background(tokens.accentBase))
            }
        }
    }
}

@Composable
private fun StatCol(label: String, value: String, modifier: Modifier = Modifier) {
    val tokens = Freela.tokens
    Column(modifier = modifier.padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = tokens.muted, style = tokens.typeExtras.monoCap)
        Spacer(Modifier.height(2.dp))
        Text(value, color = tokens.ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

private fun formatElapsed(millis: Long): String {
    val s = (millis / 1000).coerceAtLeast(0)
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60)
}

private fun formatK(v: Double): String =
    if (v >= 1000) "€${String.format(Locale.ITALIAN, "%.1f", v / 1000.0)}k"
    else "€${String.format(Locale.ITALIAN, "%.0f", v)}"
