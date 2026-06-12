package com.freela.app.ui.screens.progetti

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
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.freela.app.domain.model.StatoProgetto
import com.freela.app.ui.components.ChipSize
import com.freela.app.ui.components.ChipTone
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.FreelaChip
import com.freela.app.ui.components.FreelaProgressBar
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.PillShape
import java.util.Locale

@Composable
fun ProgettoDetailScreen(
    onBack: () -> Unit,
    viewModel: ProgettoDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens
    val c = state.cliente
    var tab by remember { mutableIntStateOf(0) }

    val orePrev = (state.progetto?.oreStimate ?: 0).toFloat()
    val oreReali = state.oreRealiMillis / 3_600_000f
    val taskTot = state.tasks.size
    val taskFatti = state.tasks.count { it.completato }
    val avanz = if (taskTot > 0) taskFatti.toFloat() / taskTot else (if (orePrev > 0) (oreReali / orePrev) else 0f)

    Column(
        modifier = Modifier.fillMaxSize().background(tokens.bg).verticalScroll(rememberScrollState()).padding(bottom = 24.dp),
    ) {
        ScreenHeader(
            title = state.progetto?.nome ?: "",
            subtitle = c?.nome ?: "—",
            large = false,
            leading = {
                IconBtn(Icons.Outlined.ArrowBack, tokens.ink) { onBack() }
            },
            trailing = {
                IconBtn(Icons.Outlined.StarBorder, tokens.muted) {}
                IconBtn(Icons.Outlined.MoreHoriz, tokens.ink) {}
            },
        )

        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)) {
            val (statoLabel, statoTone) = when (state.progetto?.stato) {
                StatoProgetto.COMPLETATO -> stringResource(R.string.progetto_stato_completato) to ChipTone.Success
                StatoProgetto.DA_INIZIARE -> stringResource(R.string.progetto_stato_da_iniziare) to ChipTone.Neutral
                else -> stringResource(R.string.progetto_stato_in_corso) to ChipTone.Accent
            }
            FreelaChip(statoLabel, tone = statoTone, dot = true, size = ChipSize.Small)
        }

        // Stat row
        Box(modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)) {
            FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(16.dp)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        StatCol(stringResource(R.string.progetto_stat_ore), "${oreReali.toInt()}h / ${orePrev.toInt()}h", Modifier.weight(1f))
                        StatCol(stringResource(R.string.progetto_stat_budget), "€${String.format(Locale.ITALIAN, "%.1f", (c?.importoPreventivato ?: 0.0) / 1000.0)}k", Modifier.weight(1f))
                        StatCol(stringResource(R.string.progetto_stat_task), "$taskFatti/$taskTot", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.progetto_avanzamento).uppercase(), color = tokens.muted, style = tokens.typeExtras.monoCap)
                        Text("${(avanz * 100).toInt()}%", color = tokens.ink, fontSize = 11.5f.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                    FreelaProgressBar(value = avanz, max = 1f, height = 6.dp)
                }
            }
        }

        // Tabs Task / Documenti
        Row(
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 6.dp)
                .fillMaxWidth()
                .clip(PillShape)
                .background(tokens.surfaceLow)
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            listOf(stringResource(R.string.progetto_tab_task), stringResource(R.string.progetto_tab_documenti)).forEachIndexed { i, label ->
                val on = i == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(PillShape)
                        .background(if (on) tokens.surface else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable { tab = i }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, color = if (on) tokens.ink else tokens.muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (tab == 0) {
            Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp)) {
                if (state.tasks.isEmpty()) {
                    Text(stringResource(R.string.progetto_no_task), color = tokens.faint, fontSize = 13.sp, modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                        Column {
                            state.tasks.forEachIndexed { i, t ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Box(
                                        modifier = Modifier.size(20.dp).clip(RoundedCornerShape(6.dp))
                                            .background(if (t.completato) tokens.accentBase else androidx.compose.ui.graphics.Color.Transparent)
                                            .border(1.5.dp, if (t.completato) tokens.accentBase else tokens.line, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (t.completato) Icon(Icons.Outlined.Check, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(13.dp))
                                    }
                                    Text(t.titolo, color = tokens.ink, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                }
                                if (i < state.tasks.size - 1) Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                stringResource(R.string.progetto_no_documenti),
                color = tokens.faint,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun IconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    val tokens = Freela.tokens
    Box(
        modifier = Modifier.size(36.dp).clip(CircleShape).border(1.dp, tokens.line, CircleShape).clickable { onClick() }.padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun StatCol(label: String, value: String, modifier: Modifier = Modifier) {
    val tokens = Freela.tokens
    Column(modifier = modifier) {
        Text(label.uppercase(), color = tokens.muted, style = tokens.typeExtras.monoCap)
        Spacer(Modifier.height(4.dp))
        Text(value, color = tokens.ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}
