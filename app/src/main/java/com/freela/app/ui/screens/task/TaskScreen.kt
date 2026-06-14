package com.freela.app.ui.screens.task

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.freela.app.domain.model.Priorita
import com.freela.app.domain.model.Task
import com.freela.app.ui.components.Avatar
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.FreelaChip
import com.freela.app.ui.components.ChipTone
import com.freela.app.ui.components.ChipSize
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.theme.Freela
import java.util.Calendar

@Composable
fun TaskScreen(
    viewModel: TaskViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens
    var filtro by remember { mutableIntStateOf(0) }
    var showNuovo by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Task?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.task_title),
            subtitle = stringResource(R.string.task_subtitle, state.totaleAperti, state.totaleUrgenti),
            trailing = {
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                R.string.task_filter_all,
                R.string.task_filter_urgenti,
                R.string.task_filter_senza_cliente,
            ).forEachIndexed { i, res ->
                FreelaChip(
                    stringResource(res),
                    tone = if (filtro == i) ChipTone.Accent else ChipTone.Neutral,
                    dot = filtro == i,
                    modifier = Modifier.clickable { filtro = i },
                )
            }
        }

        TaskGruppo.entries.forEach { gruppo ->
            val righe = state.righe.filter { it.gruppo == gruppo && passaFiltro(it, filtro) }
            if (righe.isEmpty()) return@forEach
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)) {
                SectionHead(
                    label = when (gruppo) {
                        TaskGruppo.OGGI -> stringResource(R.string.task_section_oggi)
                        TaskGruppo.SETTIMANA -> stringResource(R.string.task_section_settimana)
                    },
                    count = righe.size,
                )
                FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                    Column {
                        righe.forEachIndexed { i, r ->
                            TaskRow(r, onCheck = { viewModel.completa(r.task.id) }, onClick = { editing = r.task })
                            if (i < righe.size - 1) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNuovo) {
        TaskFormDialog(
            clienti = state.clienti,
            esistente = null,
            onDismiss = { showNuovo = false },
            onSalva = { titolo, descr, clienteId, scadenza, priorita ->
                viewModel.crea(titolo, descr, clienteId, scadenza, priorita)
                showNuovo = false
            },
            onElimina = null,
        )
    }

    editing?.let { task ->
        TaskFormDialog(
            clienti = state.clienti,
            esistente = task,
            onDismiss = { editing = null },
            onSalva = { titolo, descr, clienteId, scadenza, priorita ->
                viewModel.aggiorna(task.copy(titolo = titolo, descrizione = descr, clienteId = clienteId, scadenza = scadenza, priorita = priorita))
                editing = null
            },
            onElimina = {
                viewModel.elimina(task.id)
                editing = null
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskFormDialog(
    clienti: List<Cliente>,
    esistente: Task?,
    onDismiss: () -> Unit,
    onSalva: (titolo: String, descrizione: String?, clienteId: Long?, scadenza: Long, priorita: Priorita) -> Unit,
    onElimina: (() -> Unit)?,
) {
    val tokens = Freela.tokens
    var titolo by remember { mutableStateOf(esistente?.titolo ?: "") }
    var descrizione by remember { mutableStateOf(esistente?.descrizione ?: "") }
    var clienteId by remember { mutableStateOf(esistente?.clienteId) }
    var priorita by remember { mutableStateOf(esistente?.priorita ?: Priorita.MEDIA) }
    var giorni by remember { mutableStateOf(giorniDaScadenza(esistente?.scadenza)) }
    var ora by remember { mutableStateOf(oraDaScadenza(esistente?.scadenza)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                enabled = titolo.isNotBlank(),
                onClick = {
                    val gg = giorni.toLongOrNull() ?: 0L
                    val hh = (ora.toIntOrNull() ?: 9).coerceIn(0, 23)
                    val base = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hh); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    }
                    val scadenza = base.timeInMillis + gg * 86_400_000L
                    onSalva(titolo.trim(), descrizione.ifBlank { null }, clienteId, scadenza, priorita)
                },
            ) { Text(stringResource(R.string.task_salva)) }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (onElimina != null) {
                    TextButton(onClick = onElimina) { Text(stringResource(R.string.task_elimina), color = tokens.danger) }
                }
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.task_annulla)) }
            }
        },
        title = { Text(stringResource(if (esistente == null) R.string.task_nuovo_titolo else R.string.task_modifica_titolo)) },
        text = {
            Column {
                OutlinedTextField(
                    value = titolo,
                    onValueChange = { titolo = it },
                    label = { Text(stringResource(R.string.task_campo_titolo)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.task_campo_priorita), color = tokens.muted, style = tokens.typeExtras.monoCap)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PrioritaChip(stringResource(R.string.task_priorita_bassa), priorita == Priorita.BASSA) { priorita = Priorita.BASSA }
                    PrioritaChip(stringResource(R.string.task_priorita_media), priorita == Priorita.MEDIA) { priorita = Priorita.MEDIA }
                    PrioritaChip(stringResource(R.string.task_priorita_alta_label), priorita == Priorita.ALTA) { priorita = Priorita.ALTA }
                }
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.task_campo_cliente), color = tokens.muted, style = tokens.typeExtras.monoCap)
                Spacer(Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FreelaChip(
                        stringResource(R.string.task_cliente_nessuno),
                        tone = if (clienteId == null) ChipTone.Accent else ChipTone.Neutral,
                        size = ChipSize.Small,
                        modifier = Modifier.clickable { clienteId = null },
                    )
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = giorni,
                        onValueChange = { v -> giorni = v.filter { it.isDigit() } },
                        label = { Text(stringResource(R.string.task_campo_giorni)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = ora,
                        onValueChange = { v -> ora = v.filter { it.isDigit() }.take(2) },
                        label = { Text(stringResource(R.string.task_campo_ora)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = descrizione,
                    onValueChange = { descrizione = it },
                    label = { Text(stringResource(R.string.task_campo_descrizione)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                )
            }
        },
    )
}

@Composable
private fun PrioritaChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FreelaChip(
        label,
        tone = if (selected) ChipTone.Accent else ChipTone.Neutral,
        size = ChipSize.Small,
        modifier = Modifier.clickable { onClick() },
    )
}

private fun giorniDaScadenza(scadenza: Long?): String {
    if (scadenza == null) return "1"
    val oggi = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    return ((scadenza - oggi) / 86_400_000L).coerceAtLeast(0L).toString()
}

private fun oraDaScadenza(scadenza: Long?): String {
    if (scadenza == null) return "9"
    return Calendar.getInstance().apply { timeInMillis = scadenza }.get(Calendar.HOUR_OF_DAY).toString()
}

@Composable
private fun TaskRow(r: TaskRiga, onCheck: () -> Unit, onClick: () -> Unit) {
    val tokens = Freela.tokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(width = 1.5.dp, color = tokens.line, shape = RoundedCornerShape(6.dp))
                .clickable { onCheck() },
            contentAlignment = Alignment.Center,
        ) {
            if (r.task.completato) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = tokens.accentBase, modifier = Modifier.size(14.dp))
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(r.task.titolo, color = tokens.ink, fontSize = 14.5f.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                r.cliente?.let {
                    Avatar(name = it.nome, size = 16.dp)
                    Text(it.nome, color = tokens.muted, fontSize = 12.sp, style = MaterialTheme.typography.bodySmall)
                    Text("·", color = tokens.faint, fontSize = 12.sp)
                }
                if (r.task.priorita == com.freela.app.domain.model.Priorita.ALTA) {
                    FreelaChip(stringResource(R.string.task_priority_alta), tone = ChipTone.Danger, size = ChipSize.Small)
                }
            }
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = tokens.faint,
            modifier = Modifier.size(14.dp).align(Alignment.CenterVertically),
        )
    }
}

private fun passaFiltro(r: TaskRiga, filtro: Int): Boolean = when (filtro) {
    1 -> r.task.priorita == com.freela.app.domain.model.Priorita.ALTA
    2 -> r.cliente == null
    else -> true
}
