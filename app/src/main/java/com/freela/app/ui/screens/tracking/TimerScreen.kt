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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freela.app.R
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.SessioneLavoro
import com.freela.app.service.TimerForegroundService
import com.freela.app.ui.components.Avatar
import com.freela.app.ui.components.ChipSize
import com.freela.app.ui.components.ChipTone
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.FreelaChip
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

    var showClienteDialog by remember { mutableStateOf(false) }
    var showManuale by remember { mutableStateOf(false) }
    var mostraTutte by remember { mutableStateOf(false) }
    var sessioneDaEliminare by remember { mutableStateOf<SessioneLavoro?>(null) }

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
                        .clickable { mostraTutte = !mostraTutte }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.History, contentDescription = null, tint = if (mostraTutte) tokens.accentBase else tokens.ink, modifier = Modifier.size(16.dp))
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
            FreelaCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = state.clienti.isNotEmpty()) { showClienteDialog = true },
                padding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            ) {
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
                        Text(stringResource(R.string.timer_nessun_cliente), color = tokens.muted, modifier = Modifier.weight(1f))
                    }
                    Icon(
                        Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = tokens.faint,
                        modifier = Modifier.size(16.dp),
                    )
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
                    // Inserimento manuale di una sessione (PRD FR-19): apre il form durata/descrizione.
                    if (cliente != null) showManuale = true
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

        // Sessioni recenti
        if (state.sessioniRecenti.isNotEmpty()) {
            val visibili = if (mostraTutte) state.sessioniRecenti else state.sessioniRecenti.take(5)
            Column(modifier = Modifier.padding(horizontal = 22.dp).padding(bottom = 24.dp)) {
                SectionHead(
                    label = stringResource(R.string.timer_section_recenti),
                    count = state.sessioniRecenti.size,
                    actionText = if (mostraTutte) stringResource(R.string.timer_mostra_meno) else stringResource(R.string.cliente_action_tutte),
                    onActionClick = { mostraTutte = !mostraTutte },
                )
                FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                    Column {
                        visibili.forEachIndexed { i, s ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { sessioneDaEliminare = s }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Text(
                                    formatGiorno(s.inizio).uppercase(),
                                    color = tokens.faint,
                                    style = tokens.typeExtras.monoCap,
                                    modifier = Modifier.width(46.dp),
                                )
                                Text(
                                    s.descrizione ?: stringResource(R.string.timer_no_activity),
                                    color = tokens.ink,
                                    fontSize = 13.5f.sp,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    formatDurataSessione(s.inizio, s.fine),
                                    color = tokens.muted,
                                    style = tokens.typeExtras.monoMeta,
                                )
                            }
                            if (i < visibili.size - 1) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClienteDialog) {
        SelezionaClienteDialog(
            clienti = state.clienti,
            selezionato = state.clienteAttivo?.id,
            onDismiss = { showClienteDialog = false },
            onPick = { id ->
                viewModel.selezionaCliente(id)
                showClienteDialog = false
            },
        )
    }

    if (showManuale) {
        val cliente = state.clienteAttivo
        OreManualiDialog(
            onDismiss = { showManuale = false },
            onConferma = { minuti, descr ->
                cliente?.let { viewModel.aggiungiManuale(it.id, minuti, descr) }
                showManuale = false
            },
        )
    }

    sessioneDaEliminare?.let { s ->
        AlertDialog(
            onDismissRequest = { sessioneDaEliminare = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminaSessione(s.id)
                    sessioneDaEliminare = null
                }) { Text(stringResource(R.string.finanze_azione_elimina), color = Freela.tokens.danger) }
            },
            dismissButton = {
                TextButton(onClick = { sessioneDaEliminare = null }) { Text(stringResource(R.string.timer_annulla)) }
            },
            title = { Text(stringResource(R.string.timer_section_recenti)) },
            text = { Text(s.descrizione ?: stringResource(R.string.timer_no_activity)) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelezionaClienteDialog(
    clienti: List<Cliente>,
    selezionato: Long?,
    onDismiss: () -> Unit,
    onPick: (Long) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.timer_annulla)) }
        },
        title = { Text(stringResource(R.string.timer_seleziona_cliente)) },
        text = {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                clienti.forEach { c ->
                    FreelaChip(
                        c.nome,
                        tone = if (c.id == selezionato) ChipTone.Accent else ChipTone.Neutral,
                        size = ChipSize.Small,
                        modifier = Modifier.clickable { onPick(c.id) },
                    )
                }
            }
        },
    )
}

@Composable
private fun OreManualiDialog(
    onDismiss: () -> Unit,
    onConferma: (minuti: Int, descrizione: String?) -> Unit,
) {
    var durata by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }
    val minuti = durata.toIntOrNull() ?: 0

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = minuti > 0,
                onClick = { onConferma(minuti, descrizione.ifBlank { null }) },
            ) { Text(stringResource(R.string.timer_salva)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.timer_annulla)) }
        },
        title = { Text(stringResource(R.string.timer_manuale_titolo)) },
        text = {
            Column {
                OutlinedTextField(
                    value = durata,
                    onValueChange = { v -> durata = v.filter { it.isDigit() } },
                    label = { Text(stringResource(R.string.timer_manuale_durata)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = descrizione,
                    onValueChange = { descrizione = it },
                    label = { Text(stringResource(R.string.timer_manuale_descrizione)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                )
            }
        },
    )
}

private fun formatGiorno(millis: Long): String =
    SimpleDateFormat("EEE d", Locale.ITALIAN).format(Date(millis))

private fun formatDurataSessione(inizio: Long, fine: Long?): String {
    val durata = ((fine ?: System.currentTimeMillis()) - inizio).coerceAtLeast(0)
    val totalMin = durata / 60000
    return String.format(Locale.getDefault(), "%d:%02d", totalMin / 60, totalMin % 60)
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
