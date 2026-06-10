package com.freela.app.ui.screens.storico

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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freela.app.R
import com.freela.app.ui.components.Avatar
import com.freela.app.ui.components.BigNumber
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.FreelaChip
import com.freela.app.ui.components.ChipTone
import com.freela.app.ui.components.FreelaProgressBar
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.theme.Freela
import java.util.Locale

@Composable
fun StoricoScreen(
    onBack: () -> Unit,
    viewModel: StoricoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens
    val maxOre = state.distribuzione.maxOfOrNull { it.ore } ?: 1f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 30.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.storico_title),
            subtitle = "Ultimo mese",
            leading = {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .border(1.dp, tokens.line, CircleShape)
                        .clickable { onBack() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = tokens.ink, modifier = Modifier.size(18.dp))
                }
            },
            trailing = {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).border(1.dp, tokens.line, CircleShape).padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = tokens.ink, modifier = Modifier.size(16.dp))
                }
            },
        )

        Row(modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FreelaChip(stringResource(R.string.storico_toggle_settimana), tone = ChipTone.Neutral)
            FreelaChip(stringResource(R.string.storico_toggle_mese), tone = ChipTone.Accent, dot = true)
            FreelaChip(stringResource(R.string.storico_toggle_anno), tone = ChipTone.Neutral)
        }

        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BigNumber(
                label = stringResource(R.string.storico_stat_ore),
                value = "${String.format(Locale.ITALIAN, "%.1f", state.totOre)}h",
                sublabel = "tracciate",
                modifier = Modifier.weight(1f),
            )
            BigNumber(
                label = stringResource(R.string.storico_stat_interazioni),
                value = "${state.totInterazioni}",
                sublabel = "con clienti",
                modifier = Modifier.weight(1f),
            )
            BigNumber(
                label = stringResource(R.string.storico_stat_incassato),
                value = "€${String.format(Locale.ITALIAN, "%.1f", state.totIncassato / 1000.0)}k",
                sublabel = "questo mese",
                modifier = Modifier.weight(1f),
            )
        }

        // Distribuzione
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp)) {
            SectionHead(label = stringResource(R.string.storico_section_distribuzione))
            FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(18.dp)) {
                Column {
                    Text(stringResource(R.string.storico_label_ore_per_cliente), color = tokens.muted, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    state.distribuzione.forEach { att ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                                Avatar(name = att.cliente.nome, size = 22.dp)
                                Text(att.cliente.nome, color = tokens.ink, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Text(
                                "${String.format(Locale.ITALIAN, "%.1f", att.ore)}h",
                                color = if (att.ore == 0f) tokens.faint else tokens.ink,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                style = tokens.typeExtras.monoMeta.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                            )
                        }
                        FreelaProgressBar(value = att.ore, max = maxOre, height = 6.dp)
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        }

        // Top clienti
        if (state.topClienti.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp)) {
                SectionHead(label = stringResource(R.string.storico_section_top))
                FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                    Column {
                        state.topClienti.forEachIndexed { i, c ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                Text(
                                    "#${i + 1}",
                                    color = tokens.faint,
                                    style = tokens.typeExtras.monoMeta,
                                    modifier = Modifier.size(22.dp),
                                )
                                Avatar(name = c.nome, size = 34.dp)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(c.nome, color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        "${c.orePreventivate?.toInt() ?: 0}h tracciate",
                                        color = tokens.muted,
                                        fontSize = 12.sp,
                                    )
                                }
                                Text(
                                    "€${String.format(Locale.ITALIAN, "%,.0f", c.importoPreventivato ?: 0.0)}",
                                    color = tokens.ink,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = tokens.typeExtras.monoMeta.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                                )
                            }
                            if (i < state.topClienti.size - 1) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                            }
                        }
                    }
                }
            }
        }
    }
}
