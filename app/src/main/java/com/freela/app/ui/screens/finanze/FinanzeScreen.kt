package com.freela.app.ui.screens.finanze

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.freela.app.domain.model.StatoFatturaUi
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.FreelaChip
import com.freela.app.ui.components.ChipTone
import com.freela.app.ui.components.ChipSize
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.theme.Freela
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinanzeScreen(
    viewModel: FinanzeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens

    val mese = remember {
        val df = SimpleDateFormat("MMMM yyyy", Locale.ITALIAN)
        df.format(Date()).replaceFirstChar { it.uppercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.finanze_title),
            subtitle = mese,
            trailing = {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = tokens.muted, modifier = Modifier.size(16.dp))
                }
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(tokens.accentBase),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
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
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
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
                                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
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
