package com.freela.app.ui.screens.progetti

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freela.app.R
import com.freela.app.domain.model.Cliente
import com.freela.app.ui.components.FreelaButton
import com.freela.app.ui.components.FreelaButtonSize
import com.freela.app.ui.theme.Freela

private class TaskBozza(nome: String, ore: String) {
    var nome by mutableStateOf(nome)
    var ore by mutableStateOf(ore)
}

@Composable
fun NuovoProgettoScreen(
    onBack: () -> Unit,
    viewModel: NuovoProgettoViewModel = hiltViewModel(),
) {
    val tokens = Freela.tokens
    val clienti by viewModel.clienti.collectAsStateWithLifecycle()
    var nome by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var selectedCliente by remember { mutableStateOf<Cliente?>(null) }
    var showClienteDialog by remember { mutableStateOf(false) }
    val tasks = remember { mutableStateListOf(TaskBozza("Wireframe e UX", "12"), TaskBozza("Design UI", "20")) }
    val totale = tasks.sumOf { it.ore.toIntOrNull() ?: 0 }
    val clienteSel = selectedCliente ?: clienti.firstOrNull()

    Column(
        modifier = Modifier.fillMaxSize().background(tokens.bg).verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).border(1.dp, tokens.line, CircleShape).clickable { onBack() }.padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = tokens.ink, modifier = Modifier.size(18.dp))
            }
            Text(stringResource(R.string.np_annulla), color = tokens.muted, fontSize = 14.sp, modifier = Modifier.clickable { onBack() }.padding(6.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text(stringResource(R.string.np_eyebrow).uppercase(), color = tokens.accentBase, style = tokens.typeExtras.monoCap)
            Text(
                nome.ifBlank { stringResource(R.string.np_nome_placeholder) },
                color = if (nome.isBlank()) tokens.faint else tokens.ink,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
            )

            // Selettore cliente
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.np_cliente_label).uppercase(), color = tokens.muted, style = tokens.typeExtras.monoCap)
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(tokens.surface)
                        .border(1.dp, tokens.line, RoundedCornerShape(14.dp)).padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(Icons.Outlined.Business, contentDescription = null, tint = tokens.muted, modifier = Modifier.size(18.dp))
                    Text(
                        clienteSel?.nome ?: stringResource(R.string.np_cliente_placeholder),
                        color = if (clienteSel != null) tokens.ink else tokens.faint,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        stringResource(R.string.np_cliente_cambia),
                        color = tokens.accentBase,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { showClienteDialog = true },
                    )
                }
            }

            Field(stringResource(R.string.np_nome_label), nome, { nome = it }, stringResource(R.string.np_nome_placeholder))
            Field(stringResource(R.string.np_deadline_label), deadline, { deadline = it }, "gg/mm/aaaa")

            // Task del progetto
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.np_task_label).uppercase(), color = tokens.muted, style = tokens.typeExtras.monoCap)
                tasks.forEachIndexed { i, t ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(String.format("%02d", i + 1), color = tokens.faint, style = tokens.typeExtras.monoMeta)
                        Box(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(tokens.surface)
                                .border(1.dp, tokens.line, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 12.dp),
                        ) {
                            if (t.nome.isEmpty()) Text("Nome task", color = tokens.faint, fontSize = 14.sp)
                            BasicTextField(
                                value = t.nome, onValueChange = { t.nome = it }, singleLine = true,
                                textStyle = TextStyle(color = tokens.ink, fontSize = 14.sp),
                                cursorBrush = SolidColor(tokens.accentBase), modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Box(
                            modifier = Modifier.width(54.dp).clip(RoundedCornerShape(12.dp)).background(tokens.surface)
                                .border(1.dp, tokens.line, RoundedCornerShape(12.dp)).padding(horizontal = 10.dp, vertical = 12.dp),
                        ) {
                            BasicTextField(
                                value = t.ore, onValueChange = { v -> t.ore = v.filter { it.isDigit() } }, singleLine = true,
                                textStyle = TextStyle(color = tokens.ink, fontSize = 14.sp),
                                cursorBrush = SolidColor(tokens.accentBase),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            )
                        }
                        Text("h", color = tokens.muted, fontSize = 13.sp)
                        Box(modifier = Modifier.size(28.dp).clickable { tasks.removeAt(i) }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Close, contentDescription = null, tint = tokens.faint, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Row(
                    modifier = Modifier.clickable { tasks.add(TaskBozza("", "")) }.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = tokens.accentBase, modifier = Modifier.size(16.dp))
                    Text(stringResource(R.string.np_aggiungi_task), color = tokens.accentBase, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(tokens.accentSofter).padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(stringResource(R.string.np_totale).uppercase(), color = tokens.muted, style = tokens.typeExtras.monoCap)
                Text("${totale}h", color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            FreelaButton(
                text = stringResource(R.string.np_crea),
                onClick = { viewModel.salva(clienteSel?.id ?: 0L, nome, totale, onBack) },
                size = FreelaButtonSize.Large,
                fillMaxWidth = true,
                enabled = nome.isNotBlank() && clienteSel != null,
            )
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showClienteDialog) {
        AlertDialog(
            onDismissRequest = { showClienteDialog = false },
            confirmButton = {
                TextButton(onClick = { showClienteDialog = false }) { Text(stringResource(R.string.np_annulla)) }
            },
            title = { Text(stringResource(R.string.np_cliente_label)) },
            text = {
                Column {
                    clienti.forEach { c ->
                        Text(
                            c.nome,
                            color = tokens.ink,
                            fontSize = 15.sp,
                            modifier = Modifier.fillMaxWidth().clickable { selectedCliente = c; showClienteDialog = false }.padding(vertical = 12.dp),
                        )
                    }
                }
            },
        )
    }
}

@Composable
private fun Field(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    val tokens = Freela.tokens
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label.uppercase(), color = tokens.muted, style = tokens.typeExtras.monoCap)
        Box(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(tokens.surface)
                .border(1.dp, tokens.line, RoundedCornerShape(14.dp)).padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            if (value.isEmpty()) Text(placeholder, color = tokens.faint, fontSize = 15.sp)
            BasicTextField(
                value = value, onValueChange = onValueChange, singleLine = true,
                textStyle = TextStyle(color = tokens.ink, fontSize = 15.sp),
                cursorBrush = SolidColor(tokens.accentBase), modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
