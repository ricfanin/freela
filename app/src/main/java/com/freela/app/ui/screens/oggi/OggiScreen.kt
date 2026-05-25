package com.freela.app.ui.screens.oggi

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Search
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
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.components.StatTile
import com.freela.app.ui.components.StatTileTone
import com.freela.app.ui.theme.Freela
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun OggiScreen(
    onNavigateToCliente: (Long) -> Unit,
    onNavigateToFinanze: () -> Unit,
    onNavigateToStorico: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onStartTimer: () -> Unit,
    viewModel: OggiViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens
    val ctx = LocalContext.current

    val today = remember {
        val df = SimpleDateFormat("EEEE, d MMMM", Locale.ITALIAN)
        df.format(Date()).replaceFirstChar { it.uppercase() }
    }
    val nome = state.persona?.displayName ?: "—"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.oggi_greeting, nome),
            subtitle = today,
            leading = { Avatar(name = nome, size = 36.dp) },
            trailing = {
                IconCircle(Icons.Outlined.Search, onClick = onNavigateToStorico)
                IconCircle(Icons.Outlined.Notifications, onClick = onNavigateToSettings, showBadge = true)
            },
        )

        // Stat strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatTile(
                label = stringResource(R.string.oggi_stat_in_ritardo),
                value = formatMoneyShort(state.totRitardo),
                sub = "${state.pagamenti.count { it.giorniRitardo > 0 }} fatture",
                tone = StatTileTone.Danger,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = stringResource(R.string.oggi_stat_attesi),
                value = formatMoneyShort(state.totAttesi),
                sub = "questo mese",
                tone = StatTileTone.Accent,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = stringResource(R.string.oggi_stat_ore_settimana),
                value = "${state.totOreSettimana}h",
                sub = "3 progetti",
                tone = StatTileTone.Success,
                modifier = Modifier.weight(1f),
            )
        }

        // Suggestion card
        state.suggerimento?.let { sug ->
            Box(modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)) {
                FreelaCard(
                    padding = PaddingValues(18.dp),
                    cornerRadius = 26.dp,
                    elevated = false,
                    background = tokens.accentSofter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = tokens.accentSoft, shape = RoundedCornerShape(26.dp)),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(tokens.accentBase.copy(alpha = 0.13f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = tokens.accentBase, modifier = Modifier.size(16.dp))
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.oggi_suggestion_label).uppercase(),
                                color = tokens.accentBase,
                                style = tokens.typeExtras.monoCap,
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (sug.clienteNome != null) "${sug.testo} — ${sug.clienteNome}" else sug.testo,
                                color = tokens.ink,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FreelaButton(
                                    text = stringResource(R.string.oggi_suggestion_yes),
                                    onClick = { /* TODO PRD §11.4 fase 11 */ },
                                    size = FreelaButtonSize.Small,
                                )
                                FreelaButton(
                                    text = stringResource(R.string.oggi_suggestion_later),
                                    onClick = { /* TODO */ },
                                    variant = FreelaButtonVariant.Ghost,
                                    size = FreelaButtonSize.Small,
                                )
                            }
                        }
                    }
                }
            }
        }

        // Da contattare
        SectionWithCards(
            label = stringResource(R.string.oggi_section_contattare),
            count = state.daContattare.size,
        ) {
            state.daContattare.forEach { tc ->
                val c = tc.cliente ?: return@forEach
                FreelaCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToCliente(c.id) },
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Avatar(name = c.nome, size = 40.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(c.nome, color = tokens.ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                if (tc.task.priorita == com.freela.app.domain.model.Priorita.ALTA) {
                                    FreelaChip("urgente", tone = ChipTone.Danger, size = ChipSize.Small)
                                }
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(tc.task.titolo, color = tokens.muted, fontSize = 12.5f.sp, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconCircle(Icons.Outlined.Phone, onClick = { /* TODO intent ACTION_DIAL */ }, small = true)
                            IconCircle(Icons.Outlined.ChatBubbleOutline, onClick = { /* TODO WhatsApp intent */ }, small = true)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // Da consegnare
        SectionWithCards(
            label = stringResource(R.string.oggi_section_consegnare),
            count = state.daConsegnare.size,
        ) {
            state.daConsegnare.forEach { tc ->
                val c = tc.cliente
                FreelaCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { c?.let { onNavigateToCliente(it.id) } },
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(width = 1.5.dp, color = tokens.line, shape = CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Outlined.Description, contentDescription = null, tint = tokens.muted, modifier = Modifier.size(15.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tc.task.titolo, color = tokens.ink, fontSize = 14.5f.sp, fontWeight = FontWeight.Medium)
                            if (c != null) {
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "${c.nome} · ${formatScadenza(tc.task.scadenza)}",
                                    color = tokens.muted,
                                    fontSize = 12.sp,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = tokens.faint, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // Pagamenti
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)) {
            SectionHead(
                label = stringResource(R.string.oggi_section_pagamenti),
                count = state.pagamenti.size,
                actionText = "Finanze →",
                onActionClick = onNavigateToFinanze,
            )
            state.pagamenti.forEach { fc ->
                val c = fc.cliente
                val late = fc.giorniRitardo > 0
                FreelaCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToFinanze,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(36.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (late) tokens.danger else tokens.success),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(c?.nome ?: "—", color = tokens.ink, fontSize = 14.5f.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = formatMoney(fc.fattura.importo),
                                    color = if (late) tokens.danger else tokens.ink,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    style = tokens.typeExtras.monoMeta.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = if (late) "#${fc.fattura.numero} · scaduta ${fc.giorniRitardo}g" else "#${fc.fattura.numero}",
                                color = if (late) tokens.danger else tokens.muted,
                                fontSize = 12.sp,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SectionWithCards(label: String, count: Int, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)) {
        SectionHead(label = label, count = count, actionText = if (count > 3) "Tutti" else null)
        content()
    }
}

@Composable
private fun IconCircle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    small: Boolean = false,
    showBadge: Boolean = false,
) {
    val tokens = Freela.tokens
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(width = 1.dp, color = tokens.line, shape = CircleShape)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tokens.muted, modifier = Modifier.size(if (small) 16.dp else 18.dp))
        if (showBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(tokens.accentBase)
                    .border(width = 1.5.dp, color = tokens.surface, shape = CircleShape),
            )
        }
    }
    // Note: onClick handler omesso per semplificare il design come mostrato nel handoff (decorativi nel mock).
    // In produzione si farebbe Modifier.clickable.
}

private fun formatMoney(v: Double): String =
    "€${String.format(Locale.ITALIAN, "%,.0f", v)}"

private fun formatMoneyShort(v: Double): String =
    if (v >= 1000) "€${String.format(Locale.ITALIAN, "%.1f", v / 1000)}k"
    else "€${String.format(Locale.ITALIAN, "%.0f", v)}"

private fun formatScadenza(millis: Long): String {
    val df = SimpleDateFormat("EEEE", Locale.ITALIAN)
    val today = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0) }
    val target = Calendar.getInstance().apply { timeInMillis = millis; set(Calendar.HOUR_OF_DAY, 0) }
    val days = ((target.timeInMillis - today.timeInMillis) / 86400000L).toInt()
    return when {
        days <= 0 -> "oggi"
        days == 1 -> "domani"
        days < 7 -> df.format(Date(millis))
        else -> "fra ${days}g"
    }
}
