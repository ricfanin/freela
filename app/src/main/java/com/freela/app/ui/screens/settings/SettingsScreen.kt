package com.freela.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.outlined.BrightnessAuto
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

    // Preferenze notifiche: UI-only (non ancora persistite, PRD §11.4).
    var notifScadenze by rememberSaveable { mutableStateOf(true) }
    var notifPromemoria by rememberSaveable { mutableStateOf(true) }
    var notifRiepilogo by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.bg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
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

        // TEMA
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)) {
            SectionHead(label = stringResource(R.string.settings_section_tema))
            FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                Column {
                    val temi = listOf(
                        Triple(TemaPreferito.SISTEMA, Icons.Outlined.BrightnessAuto, stringResource(R.string.settings_tema_system) to stringResource(R.string.settings_tema_system_sub)),
                        Triple(TemaPreferito.CHIARO, Icons.Outlined.LightMode, stringResource(R.string.settings_tema_light) to stringResource(R.string.settings_tema_light_sub)),
                        Triple(TemaPreferito.SCURO, Icons.Outlined.DarkMode, stringResource(R.string.settings_tema_dark) to stringResource(R.string.settings_tema_dark_sub)),
                    )
                    temi.forEachIndexed { i, (tema, icon, labels) ->
                        SettingRow(
                            icon = icon,
                            title = labels.first,
                            subtitle = labels.second,
                            onClick = { viewModel.cambiaTema(tema) },
                            trailing = {
                                if (state.tema == tema) {
                                    Icon(Icons.Outlined.Check, contentDescription = null, tint = tokens.accentBase, modifier = Modifier.size(20.dp))
                                }
                            },
                        )
                        if (i < temi.size - 1) Divider()
                    }
                }
            }
        }

        // NOTIFICHE
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)) {
            SectionHead(label = stringResource(R.string.settings_section_notifiche))
            FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                Column {
                    SettingRow(
                        icon = Icons.Outlined.Notifications,
                        title = stringResource(R.string.settings_notif_scadenze),
                        subtitle = stringResource(R.string.settings_notif_scadenze_sub),
                        trailing = { FreelaSwitch(notifScadenze) { notifScadenze = it } },
                    )
                    Divider()
                    SettingRow(
                        icon = Icons.Outlined.Person,
                        title = stringResource(R.string.settings_notif_promemoria),
                        subtitle = stringResource(R.string.settings_notif_promemoria_sub),
                        trailing = { FreelaSwitch(notifPromemoria) { notifPromemoria = it } },
                    )
                    Divider()
                    SettingRow(
                        icon = Icons.Outlined.Schedule,
                        title = stringResource(R.string.settings_notif_riepilogo),
                        subtitle = stringResource(R.string.settings_notif_riepilogo_sub),
                        trailing = { FreelaSwitch(notifRiepilogo) { notifRiepilogo = it } },
                    )
                }
            }
        }

        // APP
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)) {
            SectionHead(label = stringResource(R.string.settings_section_app))
            FreelaCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp)) {
                SettingRow(
                    icon = Icons.Outlined.Info,
                    title = stringResource(R.string.settings_versione_label),
                    subtitle = null,
                    trailing = {
                        Text(stringResource(R.string.settings_versione_value), color = tokens.faint, style = tokens.typeExtras.monoMeta)
                    },
                )
            }
        }

        // PERSONA DEMO (strumento demo, fuori dal mockup)
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)) {
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
                                Text(persona.displayName, color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(persona.ruolo, color = tokens.muted, fontSize = 12.sp, style = MaterialTheme.typography.bodySmall)
                            }
                            when {
                                isReseeding -> CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = tokens.accentBase)
                                isSelected -> Icon(Icons.Outlined.Check, contentDescription = null, tint = tokens.accentBase, modifier = Modifier.size(20.dp))
                            }
                        }
                        if (i < PersonaDemo.entries.size - 1) Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit,
) {
    val tokens = Freela.tokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(tokens.surfaceLow),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tokens.muted, modifier = Modifier.size(17.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = tokens.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            if (subtitle != null) {
                Text(subtitle, color = tokens.muted, fontSize = 12.sp, style = MaterialTheme.typography.bodySmall)
            }
        }
        trailing()
    }
}

@Composable
private fun FreelaSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val tokens = Freela.tokens
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = tokens.accentBase,
            uncheckedTrackColor = tokens.lineSoft,
        ),
    )
}

@Composable
private fun Divider() {
    val tokens = Freela.tokens
    Box(Modifier.fillMaxWidth().height(1.dp).background(tokens.lineSoft))
}
