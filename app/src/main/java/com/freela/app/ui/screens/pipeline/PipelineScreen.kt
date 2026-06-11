package com.freela.app.ui.screens.pipeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.FasePipeline
import com.freela.app.ui.components.Avatar
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.PillShape
import com.freela.app.ui.theme.stageColor
import java.util.Locale

@Composable
fun PipelineScreen(
    onNavigateToCliente: (Long) -> Unit,
    onNuovoCliente: () -> Unit = {},
    viewModel: PipelineViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens
    val ctx = LocalContext.current

    var selected by remember { mutableStateOf<FasePipeline?>(null) }
    val fasiAttive = FasePipeline.ordered.filter { (state.clientiPerFase[it]?.isNotEmpty() == true) }
    val faseSel = selected?.takeIf { fasiAttive.contains(it) } ?: fasiAttive.firstOrNull() ?: FasePipeline.ordered.first()
    val clientiSel = state.clientiPerFase[faseSel] ?: emptyList()
    val totEur = clientiSel.sumOf { it.importoPreventivato ?: 0.0 }
    val faseIndex = FasePipeline.ordered.indexOf(faseSel) + 1

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
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(tokens.accentBase).clickable { onNuovoCliente() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.content_desc_add), tint = Color.White, modifier = Modifier.size(18.dp))
                }
            },
        )

        // Chip filtro per fase
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(fasiAttive) { fase ->
                StageFilterChip(
                    label = ctx.getString(fase.shortRes),
                    count = state.clientiPerFase[fase]?.size ?: 0,
                    color = stageColor(fase),
                    selected = fase == faseSel,
                    onClick = { selected = fase },
                )
            }
        }

        // Header fase corrente
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 14.dp, bottom = 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.pipeline_fase_di, faseIndex, FasePipeline.ordered.size),
                    color = tokens.faint,
                    style = tokens.typeExtras.monoCap,
                )
                Text(
                    text = ctx.getString(faseSel.labelRes),
                    color = tokens.ink,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = (-0.4).sp,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.pipeline_clienti_label).uppercase(),
                    color = tokens.faint,
                    style = tokens.typeExtras.monoMeta,
                )
                Text(
                    text = "${clientiSel.size} · €${String.format(Locale.ITALIAN, "%.1f", totEur / 1000.0)}k",
                    color = tokens.ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        // Lista verticale clienti della fase
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (clientiSel.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, tokens.line, RoundedCornerShape(18.dp))
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(stringResource(R.string.pipeline_empty_column), color = tokens.faint, fontSize = 12.sp)
                    }
                }
            } else {
                items(clientiSel) { c ->
                    PipelineCard(c, stageColor(faseSel), onTap = { onNavigateToCliente(c.id) })
                }
            }
        }
    }
}

@Composable
private fun StageFilterChip(
    label: String,
    count: Int,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val tokens = Freela.tokens
    Row(
        modifier = Modifier
            .clip(PillShape)
            .background(if (selected) tokens.accentBase else tokens.chipBg)
            .clickable { onClick() }
            .padding(horizontal = 11.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(if (selected) Color.White else color))
        Text(label, color = if (selected) Color.White else tokens.muted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(
            "$count",
            color = if (selected) Color.White.copy(alpha = 0.85f) else tokens.faint,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PipelineCard(c: Cliente, stageC: Color, onTap: () -> Unit) {
    val tokens = Freela.tokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(tokens.surface)
            .border(1.dp, tokens.lineSoft, RoundedCornerShape(16.dp))
            .clickable { onTap() }
            .height(IntrinsicSize.Min),
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(stageC),
        )
        Row(
            modifier = Modifier.weight(1f).padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Avatar(name = c.nome, size = 38.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(c.nome, color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                val sub = listOfNotNull(c.tags.firstOrNull()?.nome, c.fonteAcquisizione).joinToString(" · ")
                if (sub.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(sub, color = tokens.muted, fontSize = 12.sp, style = MaterialTheme.typography.bodySmall)
                }
            }
            c.importoPreventivato?.let { budget ->
                Text(
                    text = "€${String.format(Locale.ITALIAN, "%.1f", budget / 1000.0)}k",
                    color = tokens.ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = tokens.typeExtras.monoMeta.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}
