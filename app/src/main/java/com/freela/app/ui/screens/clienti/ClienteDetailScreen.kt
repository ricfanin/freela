package com.freela.app.ui.screens.clienti

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.material.icons.filled.Star
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.model.TipoInterazione
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freela.app.R
import com.freela.app.ui.components.Avatar
import com.freela.app.ui.components.FreelaButton
import com.freela.app.ui.components.FreelaButtonSize
import com.freela.app.ui.components.FreelaButtonVariant
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.FreelaChip
import com.freela.app.ui.components.ChipTone
import com.freela.app.ui.components.ChipSize
import com.freela.app.ui.components.FreelaProgressBar
import com.freela.app.ui.components.PipelineRibbon
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.components.TimelineInterazioni
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.stageColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClienteDetailScreen(
    onBack: () -> Unit,
    onStartTimer: () -> Unit,
    onNavigateToFinanze: () -> Unit = {},
    viewModel: ClienteDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens
    val cliente = state.cliente ?: return
    val ctx = LocalContext.current
    val stageC = stageColor(cliente.faseCorrente)
    var showInterazioneDialog by remember { mutableStateOf(false) }
    var showFaseDialog by remember { mutableStateOf(false) }
    var showAzioni by remember { mutableStateOf(false) }
    var showModifica by remember { mutableStateOf(false) }
    var showReminder by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg),
    ) {
        // Pattern #6 — hero gradient banner (top 220.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(tokens.accentSofter.copy(alpha = 0.67f), tokens.bg.copy(alpha = 0f)),
                        ),
                        size = Size(size.width, size.height),
                    )
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(tokens.accentBase.copy(alpha = 0.13f), tokens.bg.copy(alpha = 0f)),
                            center = Offset(size.width * 0.3f, 0f),
                            radius = size.width * 0.8f,
                        ),
                    )
                },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 30.dp),
        ) {
            // Top toolbar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconBtn(Icons.Outlined.ArrowBack, onClick = onBack)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconBtn(if (cliente.preferito) Icons.Filled.Star else Icons.Outlined.StarBorder, onClick = { viewModel.cambiaPreferito() })
                    IconBtn(Icons.Outlined.MoreHoriz, onClick = { showAzioni = true })
                }
            }

            // Header cliente
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Avatar(name = cliente.nome, size = 64.dp)
                Column {
                    Text(cliente.nome, color = tokens.ink, style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        cliente.tags.firstOrNull()?.let {
                            FreelaChip(it.nome, tone = ChipTone.Neutral, size = ChipSize.Small)
                        }
                        cliente.fonteAcquisizione?.let {
                            Text("da $it", color = tokens.muted, fontSize = 13.sp, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Quick actions
            Row(
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FreelaButton(
                    text = stringResource(R.string.cliente_action_chiama),
                    onClick = {
                        cliente.telefono?.let { tel ->
                            runCatching { ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))) }
                        }
                    },
                    variant = FreelaButtonVariant.Soft,
                    size = FreelaButtonSize.Small,
                    leading = { Icon(Icons.Outlined.Phone, contentDescription = null, modifier = Modifier.size(15.dp)) },
                )
                FreelaButton(
                    text = stringResource(R.string.cliente_action_scrivi),
                    onClick = {
                        val uri = when {
                            cliente.telefono != null -> Uri.parse("smsto:${cliente.telefono}")
                            cliente.email != null -> Uri.parse("mailto:${cliente.email}")
                            else -> null
                        }
                        uri?.let { runCatching { ctx.startActivity(Intent(Intent.ACTION_SENDTO, it)) } }
                    },
                    variant = FreelaButtonVariant.Soft,
                    size = FreelaButtonSize.Small,
                    leading = { Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(15.dp)) },
                )
                FreelaButton(
                    text = stringResource(R.string.cliente_action_timer),
                    onClick = onStartTimer,
                    variant = FreelaButtonVariant.Soft,
                    size = FreelaButtonSize.Small,
                    leading = { Icon(Icons.Outlined.PlayArrow, contentDescription = null, modifier = Modifier.size(15.dp)) },
                )
            }

            // Pipeline ribbon
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 22.dp)) {
                SectionHead(
                    label = stringResource(R.string.cliente_section_fase),
                    actionText = stringResource(R.string.cliente_action_cambia),
                    onActionClick = { showFaseDialog = true },
                )
                FreelaCard(modifier = Modifier.fillMaxWidth(), padding = 14.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(stageC))
                            Text(ctx.getString(cliente.faseCorrente.labelRes), color = tokens.ink, fontSize = 14.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            "${cliente.faseCorrente.ordine + 1} / 10",
                            color = tokens.faint,
                            style = tokens.typeExtras.monoMeta,
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    PipelineRibbon(currentStage = cliente.faseCorrente)
                }
            }

            // Prossima / Ultima
            Row(
                modifier = Modifier.padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FreelaCard(modifier = Modifier.weight(1f), padding = 14.dp) {
                    Text(
                        stringResource(R.string.cliente_section_prossima).uppercase(),
                        color = tokens.muted,
                        style = tokens.typeExtras.monoCap,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        state.prossimoTask?.titolo ?: "—",
                        color = tokens.ink,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        stringResource(R.string.cliente_action_aggiungi_reminder),
                        color = tokens.accentBase,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { showReminder = true },
                    )
                }
                FreelaCard(modifier = Modifier.weight(1f), padding = 14.dp) {
                    Text(
                        stringResource(R.string.cliente_section_ultima).uppercase(),
                        color = tokens.muted,
                        style = tokens.typeExtras.monoCap,
                    )
                    Spacer(Modifier.height(6.dp))
                    val ultima = state.timeline.firstOrNull()
                    Text(
                        if (ultima != null) "${ultima.tipo.name.lowercase().replaceFirstChar { it.uppercase() }} · ${formatRel(ultima.data)}" else "—",
                        color = tokens.ink,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(6.dp))
                    ultima?.durataMinuti?.let {
                        Text("$it min", color = tokens.muted, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }

            // Progetto (se budget)
            cliente.importoPreventivato?.let { budget ->
                Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp)) {
                    SectionHead(label = stringResource(R.string.cliente_section_progetto))
                    FreelaCard(modifier = Modifier.fillMaxWidth(), padding = 18.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            Text(stringResource(R.string.cliente_progetto_budget), color = tokens.muted, fontSize = 13.sp, lineHeight = 17.sp)
                            Text(
                                "€${String.format(Locale.ITALIAN, "%,.0f", budget)}",
                                color = tokens.ink,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                style = tokens.typeExtras.monoMeta.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                        val orePrev = cliente.orePreventivate ?: 0f
                        val oreReal = state.oreReali
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(stringResource(R.string.cliente_progetto_ore_preventivate), color = tokens.muted, fontSize = 12.sp, lineHeight = 16.sp)
                            Text(
                                "${String.format(Locale.ITALIAN, "%.1f", oreReal)}h / ${String.format(Locale.ITALIAN, "%.0f", orePrev)}h",
                                color = tokens.ink,
                                fontSize = 13.sp,
                                style = tokens.typeExtras.monoMeta.copy(fontSize = 13.sp),
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        FreelaProgressBar(value = oreReal, max = orePrev.coerceAtLeast(1f), height = 6.dp, overshoot = true)
                        if (oreReal > orePrev && orePrev > 0f) {
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "+${String.format(Locale.ITALIAN, "%.1f", oreReal - orePrev)}h oltre preventivo",
                                color = tokens.danger,
                                fontSize = 11.5f.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            // Timeline
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp)) {
                SectionHead(
                    label = stringResource(R.string.cliente_section_storico),
                    actionText = stringResource(R.string.cliente_action_aggiungi),
                    onActionClick = { showInterazioneDialog = true },
                )
                TimelineInterazioni(interazioni = state.timeline.take(5))
            }

            // Finanze inline
            if (state.fatture.isNotEmpty()) {
                Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp)) {
                    SectionHead(label = stringResource(R.string.cliente_section_finanze), actionText = stringResource(R.string.cliente_action_tutte), onActionClick = onNavigateToFinanze)
                    FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                        Column {
                            state.fatture.forEachIndexed { i, f ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column {
                                        Text(
                                            "#${f.numero}",
                                            color = tokens.ink,
                                            fontSize = 13.5f.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            style = tokens.typeExtras.monoMeta.copy(fontSize = 13.5f.sp, fontWeight = FontWeight.SemiBold),
                                        )
                                        Text("Scad. ${formatDate(f.dataScadenza)}", color = tokens.muted, fontSize = 11.5f.sp, lineHeight = 15.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        val statoUi = f.statoUi()
                                        FreelaChip(
                                            statoUi.name.lowercase(),
                                            tone = when (statoUi) {
                                                com.freela.app.domain.model.StatoFatturaUi.PAGATA -> ChipTone.Success
                                                com.freela.app.domain.model.StatoFatturaUi.IN_RITARDO -> ChipTone.Danger
                                                com.freela.app.domain.model.StatoFatturaUi.EMESSA -> ChipTone.Warning
                                            },
                                            dot = true,
                                        )
                                        Text(
                                            "€${String.format(Locale.ITALIAN, "%,.0f", f.importo)}",
                                            color = tokens.ink,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            style = tokens.typeExtras.monoMeta.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                                        )
                                    }
                                }
                                if (i < state.fatture.size - 1) {
                                    Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                                }
                            }
                        }
                    }
                }
            }

            // Note
            cliente.note?.let { note ->
                Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp)) {
                    SectionHead(label = stringResource(R.string.cliente_section_note))
                    FreelaCard(modifier = Modifier.fillMaxWidth(), padding = 16.dp) {
                        Text(note, color = tokens.ink, fontSize = 13.5f.sp, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp))
                    }
                }
            }
        }

        if (showInterazioneDialog) {
            RegistraInterazioneDialog(
                onDismiss = { showInterazioneDialog = false },
                onConferma = { tipo, descrizione, durata, conGps ->
                    viewModel.aggiungiInterazione(tipo, descrizione, durata, conGps)
                    showInterazioneDialog = false
                },
            )
        }
        if (showFaseDialog) {
            CambiaFaseDialog(
                current = cliente.faseCorrente,
                onDismiss = { showFaseDialog = false },
                onPick = { fase ->
                    viewModel.cambiaFase(fase)
                    showFaseDialog = false
                },
            )
        }
        if (showAzioni) {
            AzioniClienteDialog(
                onDismiss = { showAzioni = false },
                onModifica = { showAzioni = false; showModifica = true },
                onCambiaFase = { showAzioni = false; showFaseDialog = true },
                onElimina = { showAzioni = false; viewModel.elimina(onBack) },
            )
        }
        if (showModifica) {
            ModificaClienteDialog(
                cliente = cliente,
                onDismiss = { showModifica = false },
                onConferma = { nome, tel, email, fonte, note ->
                    viewModel.aggiornaCliente(nome, tel, email, fonte, note)
                    showModifica = false
                },
            )
        }
        if (showReminder) {
            ReminderDialog(
                onDismiss = { showReminder = false },
                onConferma = { titolo, giorni ->
                    viewModel.aggiungiReminder(titolo, giorni)
                    showReminder = false
                },
            )
        }
    }
}

@Composable
private fun AzioniClienteDialog(
    onDismiss: () -> Unit,
    onModifica: () -> Unit,
    onCambiaFase: () -> Unit,
    onElimina: () -> Unit,
) {
    val tokens = Freela.tokens
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.interazione_annulla)) }
        },
        title = { Text(stringResource(R.string.cliente_azioni_titolo)) },
        text = {
            Column {
                Text(
                    stringResource(R.string.cliente_azione_modifica),
                    color = tokens.ink,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth().clickable { onModifica() }.padding(vertical = 12.dp),
                )
                Text(
                    stringResource(R.string.cliente_action_cambia),
                    color = tokens.ink,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth().clickable { onCambiaFase() }.padding(vertical = 12.dp),
                )
                Text(
                    stringResource(R.string.cliente_azione_elimina),
                    color = tokens.danger,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth().clickable { onElimina() }.padding(vertical = 12.dp),
                )
            }
        },
    )
}

@Composable
private fun ModificaClienteDialog(
    cliente: com.freela.app.domain.model.Cliente,
    onDismiss: () -> Unit,
    onConferma: (nome: String, telefono: String?, email: String?, fonte: String?, note: String?) -> Unit,
) {
    var nome by remember { mutableStateOf(cliente.nome) }
    var telefono by remember { mutableStateOf(cliente.telefono ?: "") }
    var email by remember { mutableStateOf(cliente.email ?: "") }
    var fonte by remember { mutableStateOf(cliente.fonteAcquisizione ?: "") }
    var note by remember { mutableStateOf(cliente.note ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = nome.isNotBlank(),
                onClick = { onConferma(nome, telefono, email, fonte, note) },
            ) { Text(stringResource(R.string.interazione_salva)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.interazione_annulla)) }
        },
        title = { Text(stringResource(R.string.cliente_azione_modifica)) },
        text = {
            Column {
                OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text(stringResource(R.string.nc_nome_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text(stringResource(R.string.nc_telefono_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.nc_email_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = fonte, onValueChange = { fonte = it }, label = { Text(stringResource(R.string.nc_fonte_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text(stringResource(R.string.nc_note_label)) }, modifier = Modifier.fillMaxWidth(), singleLine = false)
            }
        },
    )
}

@Composable
private fun ReminderDialog(
    onDismiss: () -> Unit,
    onConferma: (titolo: String, giorni: Int) -> Unit,
) {
    var titolo by remember { mutableStateOf("") }
    var giorni by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = titolo.isNotBlank(),
                onClick = { onConferma(titolo, giorni.toIntOrNull() ?: 1) },
            ) { Text(stringResource(R.string.interazione_salva)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.interazione_annulla)) }
        },
        title = { Text(stringResource(R.string.cliente_reminder_titolo)) },
        text = {
            Column {
                OutlinedTextField(value = titolo, onValueChange = { titolo = it }, label = { Text(stringResource(R.string.cliente_reminder_cosa)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = giorni,
                    onValueChange = { v -> giorni = v.filter { it.isDigit() } },
                    label = { Text(stringResource(R.string.cliente_reminder_giorni)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CambiaFaseDialog(
    current: FasePipeline,
    onDismiss: () -> Unit,
    onPick: (FasePipeline) -> Unit,
) {
    val ctx = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.interazione_annulla)) }
        },
        title = { Text(stringResource(R.string.cliente_action_cambia)) },
        text = {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FasePipeline.ordered.forEach { fase ->
                    FreelaChip(
                        ctx.getString(fase.shortRes),
                        tone = if (fase == current) ChipTone.Accent else ChipTone.Neutral,
                        size = ChipSize.Small,
                        modifier = Modifier.clickable { onPick(fase) },
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RegistraInterazioneDialog(
    onDismiss: () -> Unit,
    onConferma: (TipoInterazione, String?, Int?, Boolean) -> Unit,
) {
    val tokens = Freela.tokens
    val ctx = LocalContext.current
    var tipo by remember { mutableStateOf(TipoInterazione.CALL) }
    var descrizione by remember { mutableStateOf("") }
    var durata by remember { mutableStateOf("") }
    var gpsAbilitato by remember { mutableStateOf(false) }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { concesso -> gpsAbilitato = concesso }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConferma(
                    tipo,
                    descrizione.ifBlank { null },
                    durata.toIntOrNull(),
                    gpsAbilitato && tipo == TipoInterazione.MEETING,
                )
            }) { Text(stringResource(R.string.interazione_salva)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.interazione_annulla)) }
        },
        title = { Text(stringResource(R.string.interazione_titolo)) },
        text = {
            Column {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TipoInterazione.entries.forEach { t ->
                        FreelaChip(
                            t.name.lowercase().replaceFirstChar { it.uppercase() },
                            tone = if (t == tipo) ChipTone.Accent else ChipTone.Neutral,
                            size = ChipSize.Small,
                            modifier = Modifier.clickable {
                                tipo = t
                                if (t != TipoInterazione.MEETING) gpsAbilitato = false
                            },
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = descrizione,
                    onValueChange = { descrizione = it },
                    label = { Text(stringResource(R.string.interazione_descrizione)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = durata,
                    onValueChange = { v -> durata = v.filter { it.isDigit() } },
                    label = { Text(stringResource(R.string.interazione_durata)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                if (tipo == TipoInterazione.MEETING) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(R.string.interazione_tag_gps), color = tokens.ink, fontSize = 14.sp)
                        Switch(
                            checked = gpsAbilitato,
                            onCheckedChange = { abilita ->
                                if (abilita) {
                                    val concesso = ContextCompat.checkSelfPermission(
                                        ctx, Manifest.permission.ACCESS_FINE_LOCATION,
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (concesso) gpsAbilitato = true
                                    else permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                } else {
                                    gpsAbilitato = false
                                }
                            },
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun IconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    val tokens = Freela.tokens
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(width = 1.dp, color = tokens.line, shape = CircleShape)
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tokens.ink, modifier = Modifier.size(18.dp))
    }
}

private val dateDf = SimpleDateFormat("d MMM", Locale.ITALIAN)
private fun formatDate(millis: Long): String = dateDf.format(Date(millis))

private fun formatRel(millis: Long): String {
    val days = ((System.currentTimeMillis() - millis) / 86400000L).toInt()
    return when {
        days <= 0 -> "oggi"
        days == 1 -> "ieri"
        days < 7 -> "${days}g fa"
        else -> formatDate(millis)
    }
}
