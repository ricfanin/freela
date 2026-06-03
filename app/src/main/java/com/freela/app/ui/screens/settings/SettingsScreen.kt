package com.freela.app.ui.screens.settings

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.freela.app.domain.model.PersonaDemo
import com.freela.app.domain.repository.TemaPreferito
import com.freela.app.ui.components.FreelaCard
import com.freela.app.ui.components.ScreenHeader
import com.freela.app.ui.components.SectionHead
import com.freela.app.ui.theme.Freela

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tokens = Freela.tokens

    Column(
        modifier = Modifier.fillMaxSize().background(tokens.bg),
    ) {
        ScreenHeader(
            title = stringResource(R.string.settings_title),
            leading = {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).border(1.dp, tokens.line, CircleShape).clickable { onBack() }.padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = tokens.ink, modifier = Modifier.size(18.dp))
                }
            },
        )

        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp)) {
            SectionHead(label = stringResource(R.string.settings_section_persona))
            FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                Column {
                    PersonaDemo.entries.forEachIndexed { i, persona ->
                        val isSelected = state.personaCorrente == persona
                        val isReseeding = state.reseedingPersona == persona
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = state.reseedingPersona == null) { viewModel.cambiaPersona(persona) }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    persona.displayName,
                                    color = tokens.ink,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    persona.ruolo,
                                    color = tokens.muted,
                                    fontSize = 12.sp,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            when {
                                isReseeding -> CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = tokens.accentBase)
                                isSelected -> Icon(Icons.Outlined.Check, contentDescription = null, tint = tokens.accentBase, modifier = Modifier.size(20.dp))
                            }
                        }
                        if (i < PersonaDemo.entries.size - 1) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp)) {
            SectionHead(label = stringResource(R.string.settings_section_tema))
            FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                Column {
                    val temi = listOf(
                        TemaPreferito.SISTEMA to stringResource(R.string.settings_tema_system),
                        TemaPreferito.CHIARO to stringResource(R.string.settings_tema_light),
                        TemaPreferito.SCURO to stringResource(R.string.settings_tema_dark),
                    )
                    temi.forEachIndexed { i, (tema, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.cambiaTema(tema) }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(label, color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            if (state.tema == tema) {
                                Icon(Icons.Outlined.Check, contentDescription = null, tint = tokens.accentBase, modifier = Modifier.size(20.dp))
                            }
                        }
                        if (i < temi.size - 1) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
                        }
                    }
                }
            }
        }
    }
}
