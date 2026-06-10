package com.freela.app.ui.screens.clienti

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freela.app.R
import com.freela.app.domain.model.Cliente
import com.freela.app.ui.components.Avatar
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.PillShape
import com.freela.app.ui.theme.stageColor
import java.util.Locale

@Composable
fun ClientiScreen(
    onNavigateToCliente: (Long) -> Unit,
    viewModel: ClientiViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val tokens = Freela.tokens
    val ctx = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg),
    ) {
        ScreenHeader(
            title = stringResource(R.string.clienti_title),
            subtitle = stringResource(R.string.clienti_subtitle, state.totaleAttivi, state.totaleRicorrenti),
            trailing = {
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

        // Search bar pill
        Row(
            modifier = Modifier
                .padding(horizontal = 22.dp, vertical = 4.dp)
                .fillMaxWidth()
                .clip(PillShape)
                .background(tokens.chipBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = tokens.muted, modifier = Modifier.size(17.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.clienti_search_placeholder),
                        color = tokens.faint,
                        fontSize = 14.sp,
                    )
                }
                BasicTextField(
                    value = TextFieldValue(query),
                    onValueChange = { viewModel.aggiornaQuery(it.text) },
                    textStyle = TextStyle(color = tokens.ink, fontSize = 14.sp),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(tokens.accentBase),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Lista clienti raggruppata
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 22.dp, vertical = 4.dp),
        ) {
            state.sezioni.toSortedMap().forEach { (lettera, clienti) ->
                item {
                    Text(
                        text = lettera.toString(),
                        color = tokens.faint,
                        style = tokens.typeExtras.monoCap,
                        modifier = Modifier.padding(start = 4.dp, top = 8.dp, bottom = 8.dp),
                    )
                }
                items(clienti.size) { i ->
                    val c = clienti[i]
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = tokens.surface,
                        shadowElevation = 2.dp,
                    ) {
                        ClienteRow(c, onClick = { onNavigateToCliente(c.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ClienteRow(c: Cliente, onClick: () -> Unit) {
    val tokens = Freela.tokens
    val ctx = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Avatar(name = c.nome, size = 42.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(c.nome, color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(7.dp).clip(CircleShape).background(stageColor(c.faseCorrente)))
                Text(ctx.getString(c.faseCorrente.shortRes), color = tokens.muted, fontSize = 12.5f.sp, style = MaterialTheme.typography.bodySmall)
            }
        }
        c.importoPreventivato?.let { v ->
            Text(
                text = "€${String.format(Locale.ITALIAN, "%.1f", v / 1000.0)}k",
                color = tokens.muted,
                fontSize = 13.sp,
                style = tokens.typeExtras.monoMeta.copy(fontSize = 13.sp),
            )
        }
    }
}
