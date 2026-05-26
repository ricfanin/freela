package com.freela.app.ui.screens.pipeline

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freela.app.R
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.FasePipeline
import com.freela.app.ui.components.Avatar
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.FreelaChip
import com.freela.app.ui.components.ChipTone
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.stageColor
import java.util.Locale

@Composable
fun PipelineScreen(
    onNavigateToCliente: (Long) -> Unit,
    viewModel: PipelineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg),
    ) {
        ScreenHeader(
            title = stringResource(R.string.pipeline_title),
            subtitle = stringResource(R.string.pipeline_subtitle, state.totaleClienti, state.fasiAttive),
            trailing = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(width = 1.dp, color = tokens.line, shape = CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.FilterAlt, contentDescription = stringResource(R.string.content_desc_filter), tint = tokens.muted, modifier = Modifier.size(16.dp))
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(tokens.accentBase),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.content_desc_add), tint = Color.White, modifier = Modifier.size(18.dp))
                }
            },
        )

        // View toggle
        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FreelaChip(stringResource(R.string.pipeline_view_kanban), tone = ChipTone.Accent, dot = true)
            FreelaChip(stringResource(R.string.pipeline_view_lista), tone = ChipTone.Neutral)
            FreelaChip(stringResource(R.string.pipeline_view_calendario), tone = ChipTone.Neutral)
        }

        Spacer(Modifier.height(14.dp))

        // Kanban
        LazyRow(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(FasePipeline.ordered) { fase ->
                KanbanColumn(
                    fase = fase,
                    clienti = state.clientiPerFase[fase] ?: emptyList(),
                    onTapCliente = onNavigateToCliente,
                )
            }
        }
    }
}

@Composable
private fun KanbanColumn(
    fase: FasePipeline,
    clienti: List<Cliente>,
    onTapCliente: (Long) -> Unit,
) {
    val tokens = Freela.tokens
    val ctx = LocalContext.current
    val color = stageColor(fase)

    Column(
        modifier = Modifier.width(240.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(color))
                Text(
                    text = ctx.getString(fase.shortRes).uppercase(),
                    color = tokens.ink,
                    style = tokens.typeExtras.monoCap,
                )
            }
            Text(
                text = "${clienti.size}",
                color = tokens.faint,
                style = tokens.typeExtras.monoMeta,
            )
        }

        if (clienti.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.5.dp, color = tokens.line, shape = RoundedCornerShape(18.dp))
                    .padding(22.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(R.string.pipeline_empty_column), color = tokens.faint, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(clienti) { c ->
                    PipelineCard(c, color, onTap = { onTapCliente(c.id) })
                }
            }
        }
    }
}

@Composable
private fun PipelineCard(c: Cliente, stageC: Color, onTap: () -> Unit) {
    val tokens = Freela.tokens
    val bg = lerp(tokens.surface, stageC, 0.06f)
    FreelaCard(
        modifier = Modifier.fillMaxWidth(),
        background = bg,
        padding = PaddingValues(0.dp),
        onClick = onTap,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Pattern #2 — barretta sinistra 3.dp stage color
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(stageC),
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Avatar(name = c.nome, size = 34.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(c.nome, color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        val sub = listOfNotNull(c.tags.firstOrNull()?.nome, c.fonteAcquisizione).joinToString(" · ")
                        if (sub.isNotEmpty()) {
                            Text(sub, color = tokens.muted, fontSize = 11.5f.sp, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                c.importoPreventivato?.let { budget ->
                    Text(
                        text = "€${String.format(Locale.ITALIAN, "%,.0f", budget)}",
                        color = tokens.ink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 8.dp),
                        style = tokens.typeExtras.monoMeta.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                    )
                }
            }
        }
    }
}

