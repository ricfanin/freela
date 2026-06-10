package com.freela.app.ui.screens.progetti

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.freela.app.ui.components.ChipTone
import com.freela.app.ui.components.FreelaChip
import com.freela.app.ui.components.FreelaProgressBar
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.theme.Freela

@Composable
fun ProgettiScreen(
    onNavigateToProgetto: (Long) -> Unit,
    viewModel: ProgettiViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens

    Column(
        modifier = Modifier.fillMaxSize().background(tokens.bg),
    ) {
        ScreenHeader(
            title = stringResource(R.string.progetti_title),
            subtitle = stringResource(R.string.progetti_subtitle, state.aperti.size, state.completati.size),
            trailing = {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(tokens.accentBase),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.content_desc_add), tint = Color.White, modifier = Modifier.size(18.dp))
                }
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FreelaChip(stringResource(R.string.progetti_filter_tutti), tone = ChipTone.Accent, dot = true)
            FreelaChip(stringResource(R.string.progetti_filter_in_corso), tone = ChipTone.Neutral)
            FreelaChip(stringResource(R.string.progetti_filter_da_iniziare), tone = ChipTone.Neutral)
            FreelaChip(stringResource(R.string.progetti_filter_in_scadenza), tone = ChipTone.Neutral)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (state.aperti.isNotEmpty()) {
                item {
                    SectionHead(label = stringResource(R.string.progetti_section_aperti), count = state.aperti.size)
                }
                items(state.aperti.size) { i -> ProgettoCard(state.aperti[i], onClick = { onNavigateToProgetto(state.aperti[i].clienteId) }) }
            }
            if (state.completati.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    SectionHead(label = stringResource(R.string.progetti_section_completati), count = state.completati.size)
                }
                items(state.completati.size) { i -> ProgettoCard(state.completati[i], onClick = { onNavigateToProgetto(state.completati[i].clienteId) }) }
            }
        }
    }
}

@Composable
private fun ProgettoCard(p: ProgettoUi, onClick: () -> Unit) {
    val tokens = Freela.tokens
    val overshoot = p.percentuale > 100
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = tokens.surface,
        shadowElevation = 2.dp,
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(p.cliente, color = tokens.faint, fontSize = 11.5f.sp)
            Spacer(Modifier.height(2.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(p.nome, color = tokens.ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    "${p.percentuale}%",
                    color = if (overshoot) tokens.danger else tokens.ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    style = tokens.typeExtras.monoMeta.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val (label, tone) = when (p.stato) {
                    StatoProgetto.IN_CORSO -> stringResource(R.string.progetto_stato_in_corso) to ChipTone.Accent
                    StatoProgetto.DA_INIZIARE -> stringResource(R.string.progetto_stato_da_iniziare) to ChipTone.Neutral
                    StatoProgetto.COMPLETATO -> stringResource(R.string.progetto_stato_completato) to ChipTone.Success
                }
                FreelaChip(label, tone = tone, dot = true)
                Text(
                    stringResource(R.string.progetto_n_task, p.taskTotali),
                    color = tokens.muted,
                    fontSize = 12.sp,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "${p.oreReali.toInt()}h / ${p.orePrev.toInt()}h",
                    color = tokens.faint,
                    style = tokens.typeExtras.monoMeta,
                )
            }
            Spacer(Modifier.height(10.dp))
            FreelaProgressBar(
                value = p.oreReali,
                max = p.orePrev.coerceAtLeast(1f),
                height = 6.dp,
                color = if (overshoot) tokens.danger else tokens.accentBase,
            )
        }
    }
}
