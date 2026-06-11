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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freela.app.R
import com.freela.app.ui.components.Avatar
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.FreelaChip
import com.freela.app.ui.components.ChipTone
import com.freela.app.ui.components.ChipSize
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.theme.Freela

@Composable
fun TaskScreen(
    viewModel: TaskViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens
    var filtro by remember { mutableIntStateOf(0) }

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
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(tokens.accentBase),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
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
                R.string.task_filter_suggeriti,
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
                        TaskGruppo.SUGGERITI -> stringResource(R.string.task_section_suggeriti)
                    },
                    count = righe.size,
                )
                FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                    Column {
                        righe.forEachIndexed { i, r ->
                            TaskRow(r, onCheck = { viewModel.completa(r.task.id) })
                            if (i < righe.size - 1) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(r: TaskRiga, onCheck: () -> Unit) {
    val tokens = Freela.tokens
    val sugg = r.gruppo == TaskGruppo.SUGGERITI
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (sugg) tokens.accentSofter else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Checkbox
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (sugg) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = tokens.accentBase, modifier = Modifier.size(11.dp))
                        Text(
                            stringResource(R.string.task_badge_suggerito).uppercase(),
                            color = tokens.accentBase,
                            style = tokens.typeExtras.monoCap,
                        )
                    }
                }
                Text(r.task.titolo, color = tokens.ink, fontSize = 14.5f.sp, fontWeight = FontWeight.Medium)
            }
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
    2 -> r.gruppo == TaskGruppo.SUGGERITI
    3 -> r.cliente == null
    else -> true
}
