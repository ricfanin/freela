package com.freela.app.ui.screens.finanze

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freela.app.R
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.StatoFatturaUi
import com.freela.app.domain.model.StatoPreventivo
import com.freela.app.ui.components.ChipSize
import com.freela.app.ui.components.ChipTone
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.FreelaChip
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.theme.Freela
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun FinanzeScreen(
    viewModel: FinanzeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens

    var showNuovo by remember { mutableStateOf(false) }
    var showMese by remember { mutableStateOf(false) }
    var fatturaAzioni by remember { mutableStateOf<FatturaRiga?>(null) }
    var preventivoAzioni by remember { mutableStateOf<PreventivoRiga?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.finanze_title),
            subtitle = state.meseLabel,
            trailing = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .clickable { showMese = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = tokens.muted, modifier = Modifier.size(16.dp))
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(tokens.accentBase)
                        .clickable { showNuovo = true },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.content_desc_add), tint = Color.White, modifier = Modifier.size(18.dp))
                }
            },
        )

        // Hero card
        Box(modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)) {
            FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(20.dp)) {
                Column {
                    Text(
                        stringResource(R.string.finanze_hero_label).uppercase(),
                        color = tokens.muted,
                        style = tokens.typeExtras.monoCap,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "€${String.format(Locale.ITALIAN, "%,.0f", state.fatturatoMese)}",
                        color = tokens.ink,
                        style = tokens.typeExtras.displayNum,
                    )
                    Spacer(Modifier.height(18.dp))
                    // Stacked bar
                    val total = state.fatturatoMese.coerceAtLeast(1.0)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(tokens.lineSoft),
                    ) {
                        if (state.incassato > 0) {
                            Box(Modifier.fillMaxWidth((state.incassato / total).toFloat()).fillMaxHeight().background(tokens.success))
                        }
                        if (state.attesi > 0) {
                            Box(Modifier.fillMaxWidth((state.attesi / total).toFloat()).fillMaxHeight().background(tokens.accentBase))
                        }
                        if (state.inRitardo > 0) {
                            Box(Modifier.fillMaxWidth((state.inRitardo / total).toFloat()).fillMaxHeight().background(tokens.danger))
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Legend(tokens.success, stringResource(R.string.finanze_legend_incassato), formatMoney(state.incassato))
                        Legend(tokens.accentBase, stringResource(R.string.finanze_legend_attesi), formatMoney(state.attesi))
                        Legend(tokens.danger, stringResource(R.string.finanze_legend_in_ritardo), formatMoney(state.inRitardo))
                    }
                }
            }
        }

        // Fatture
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp)) {
            SectionHead(label = stringResource(R.string.finanze_section_fatture), count = state.fatture.size)
            Spacer(Modifier.height(12.dp))
            FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                Column {
                    state.fatture.forEachIndexed { i, riga ->
                        val color = when (riga.statoUi) {
                            StatoFatturaUi.PAGATA -> tokens.success
                            StatoFatturaUi.IN_RITARDO -> tokens.danger
                            StatoFatturaUi.EMESSA -> tokens.accentBase
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { fatturaAzioni = riga }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(color),
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(riga.cliente?.nome ?: "—", color = tokens.ink, fontSize = 13.5f.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "#${riga.fattura.numero}",
                                    color = tokens.muted,
                                    fontSize = 11.5f.sp,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "€${String.format(Locale.ITALIAN, "%,.0f", riga.fattura.importo)}",
                                    color = if (riga.statoUi == StatoFatturaUi.IN_RITARDO) tokens.danger else tokens.ink,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = tokens.typeExtras.monoMeta.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                                )
                                Text(
                                    text = when (riga.statoUi) {
                                        StatoFatturaUi.PAGATA -> stringResource(R.string.finanze_status_pagata)
                                        StatoFatturaUi.IN_RITARDO -> stringResource(R.string.finanze_status_ritardo)
                                        StatoFatturaUi.EMESSA -> stringResource(R.string.finanze_status_emessa)
                                    }.uppercase(),
                                    color = color,
                                    style = tokens.typeExtras.monoCap,
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

        // Preventivi
        if (state.preventivi.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp)) {
                SectionHead(label = stringResource(R.string.finanze_section_preventivi), count = state.preventivi.size)
                FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(16.dp)) {
                    Column {
                        state.preventivi.forEachIndexed { i, riga ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { preventivoAzioni = riga }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column {
                                    Text(riga.cliente?.nome ?: "—", color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Spacer(Modifier.height(4.dp))
                                    FreelaChip(
                                        riga.preventivo.stato.name.lowercase().replace('_', ' '),
                                        tone = ChipTone.Warning,
                                        size = ChipSize.Small,
                                        dot = true,
                                    )
                                }
                                Text(
                                    "€${String.format(Locale.ITALIAN, "%,.0f", riga.preventivo.importo)}",
                                    color = tokens.ink,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = tokens.typeExtras.monoMeta.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                                )
                            }
                            if (i < state.preventivi.size - 1) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNuovo) {
        NuovaVoceFinanzaDialog(
            clienti = state.clienti,
            suggerimentoNumero = numeroSuggerito(state.fatture.size),
            onDismiss = { showNuovo = false },
            onFattura = { numero, clienteId, importo, scadenza ->
                viewModel.creaFattura(numero, clienteId, importo, scadenza)
                showNuovo = false
            },
            onPreventivo = { clienteId, importo, note ->
                viewModel.creaPreventivo(clienteId, importo, note)
                showNuovo = false
            },
        )
    }

    if (showMese) {
        MeseDialog(
            selezionato = state.meseOffset,
            onDismiss = { showMese = false },
            onPick = { offset ->
                viewModel.selezionaMese(offset)
                showMese = false
            },
        )
    }

    fatturaAzioni?.let { riga ->
        AzioniFatturaDialog(
            riga = riga,
            onDismiss = { fatturaAzioni = null },
            onSegnaPagata = { viewModel.segnaPagata(riga.fattura.id); fatturaAzioni = null },
            onElimina = { viewModel.eliminaFattura(riga.fattura.id); fatturaAzioni = null },
        )
    }

    preventivoAzioni?.let { riga ->
        AzioniPreventivoDialog(
            riga = riga,
            onDismiss = { preventivoAzioni = null },
            onAccetta = { viewModel.cambiaStatoPreventivo(riga.preventivo.id, StatoPreventivo.ACCETTATO); preventivoAzioni = null },
            onRifiuta = { viewModel.cambiaStatoPreventivo(riga.preventivo.id, StatoPreventivo.RIFIUTATO); preventivoAzioni = null },
            onElimina = { viewModel.eliminaPreventivo(riga.preventivo.id); preventivoAzioni = null },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NuovaVoceFinanzaDialog(
    clienti: List<Cliente>,
    suggerimentoNumero: String,
    onDismiss: () -> Unit,
    onFattura: (numero: String, clienteId: Long, importo: Double, dataScadenza: Long) -> Unit,
    onPreventivo: (clienteId: Long, importo: Double, note: String?) -> Unit,
) {
    var isFattura by remember { mutableStateOf(true) }
    var clienteId by remember { mutableStateOf(clienti.firstOrNull()?.id) }
    var importo by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf(suggerimentoNumero) }
    var giorni by remember { mutableStateOf("30") }
    var note by remember { mutableStateOf("") }

    val importoVal = importo.replace(',', '.').toDoubleOrNull() ?: 0.0
    val valido = clienteId != null && importoVal > 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = {
                    val cid = clienteId ?: return@TextButton
                    if (isFattura) {
                        val gg = giorni.toLongOrNull() ?: 30L
                        val scadenza = System.currentTimeMillis() + gg * 86_400_000L
                        onFattura(numero.ifBlank { suggerimentoNumero }, cid, importoVal, scadenza)
                    } else {
                        onPreventivo(cid, importoVal, note.ifBlank { null })
                    }
                },
            ) { Text(stringResource(R.string.finanze_salva)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.finanze_annulla)) }
        },
        title = { Text(stringResource(R.string.finanze_nuovo_titolo)) },
        text = {
            if (clienti.isEmpty()) {
                Text(stringResource(R.string.finanze_nessun_cliente))
            } else {
                Column {
                    // Tipo
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FreelaChip(
                            stringResource(R.string.finanze_tipo_fattura),
                            tone = if (isFattura) ChipTone.Accent else ChipTone.Neutral,
                            size = ChipSize.Small,
                            modifier = Modifier.clickable { isFattura = true },
                        )
                        FreelaChip(
                            stringResource(R.string.finanze_tipo_preventivo),
                            tone = if (!isFattura) ChipTone.Accent else ChipTone.Neutral,
                            size = ChipSize.Small,
                            modifier = Modifier.clickable { isFattura = false },
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.finanze_campo_cliente), color = Freela.tokens.muted, style = Freela.tokens.typeExtras.monoCap)
                    Spacer(Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        clienti.forEach { c ->
                            FreelaChip(
                                c.nome,
                                tone = if (c.id == clienteId) ChipTone.Accent else ChipTone.Neutral,
                                size = ChipSize.Small,
                                modifier = Modifier.clickable { clienteId = c.id },
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = importo,
                        onValueChange = { v -> importo = v.filter { it.isDigit() || it == '.' || it == ',' } },
                        label = { Text(stringResource(R.string.finanze_campo_importo)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    if (isFattura) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = numero,
                            onValueChange = { numero = it },
                            label = { Text(stringResource(R.string.finanze_campo_numero)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = giorni,
                            onValueChange = { v -> giorni = v.filter { it.isDigit() } },
                            label = { Text(stringResource(R.string.finanze_campo_giorni)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    } else {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text(stringResource(R.string.finanze_campo_note)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = false,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun AzioniFatturaDialog(
    riga: FatturaRiga,
    onDismiss: () -> Unit,
    onSegnaPagata: () -> Unit,
    onElimina: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.finanze_annulla)) }
        },
        title = { Text(stringResource(R.string.finanze_azioni_fattura, riga.fattura.numero)) },
        text = {
            Column {
                if (riga.statoUi != StatoFatturaUi.PAGATA) {
                    Text(
                        stringResource(R.string.finanze_azione_segna_pagata),
                        color = Freela.tokens.success,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth().clickable { onSegnaPagata() }.padding(vertical = 12.dp),
                    )
                }
                Text(
                    stringResource(R.string.finanze_azione_elimina),
                    color = Freela.tokens.danger,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth().clickable { onElimina() }.padding(vertical = 12.dp),
                )
            }
        },
    )
}

@Composable
private fun AzioniPreventivoDialog(
    riga: PreventivoRiga,
    onDismiss: () -> Unit,
    onAccetta: () -> Unit,
    onRifiuta: () -> Unit,
    onElimina: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.finanze_annulla)) }
        },
        title = { Text(stringResource(R.string.finanze_azioni_preventivo)) },
        text = {
            Column {
                if (riga.preventivo.stato != StatoPreventivo.ACCETTATO) {
                    Text(
                        stringResource(R.string.finanze_prev_accetta),
                        color = Freela.tokens.success,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth().clickable { onAccetta() }.padding(vertical = 12.dp),
                    )
                }
                Text(
                    stringResource(R.string.finanze_prev_rifiuta),
                    color = Freela.tokens.muted,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth().clickable { onRifiuta() }.padding(vertical = 12.dp),
                )
                Text(
                    stringResource(R.string.finanze_azione_elimina),
                    color = Freela.tokens.danger,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth().clickable { onElimina() }.padding(vertical = 12.dp),
                )
            }
        },
    )
}

@Composable
private fun MeseDialog(
    selezionato: Int,
    onDismiss: () -> Unit,
    onPick: (Int) -> Unit,
) {
    val df = remember { SimpleDateFormat("MMMM yyyy", Locale.ITALIAN) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.finanze_annulla)) }
        },
        title = { Text(stringResource(R.string.finanze_mese_titolo)) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                (0 downTo -11).forEach { offset ->
                    val cal = Calendar.getInstance().apply { add(Calendar.MONTH, offset) }
                    val label = df.format(cal.time).replaceFirstChar { it.uppercase() }
                    Text(
                        label,
                        color = if (offset == selezionato) Freela.tokens.accentBase else Freela.tokens.ink,
                        fontWeight = if (offset == selezionato) FontWeight.SemiBold else FontWeight.Normal,
                        modifier = Modifier.fillMaxWidth().clickable { onPick(offset) }.padding(vertical = 10.dp),
                    )
                }
            }
        },
    )
}

@Composable
private fun Legend(color: Color, label: String, value: String) {
    val tokens = Freela.tokens
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(color))
            Text(label, color = tokens.muted, fontSize = 11.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            color = tokens.ink,
            fontSize = 13.5f.sp,
            fontWeight = FontWeight.SemiBold,
            style = tokens.typeExtras.monoMeta.copy(fontSize = 13.5f.sp, fontWeight = FontWeight.SemiBold),
        )
    }
}

private fun formatMoney(v: Double): String = "€${String.format(Locale.ITALIAN, "%,.0f", v)}"

private fun numeroSuggerito(numFatture: Int): String {
    val anno = Calendar.getInstance().get(Calendar.YEAR)
    return "$anno-${String.format(Locale.ITALIAN, "%03d", numFatture + 1)}"
}
