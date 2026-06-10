package com.freela.app.ui.screens.clienti

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.freela.app.R
import com.freela.app.ui.components.Avatar
import com.freela.app.ui.components.FreelaButton
import com.freela.app.ui.components.FreelaButtonSize
import com.freela.app.ui.theme.Freela
import androidx.compose.ui.res.stringResource

@Composable
fun NuovoClienteScreen(
    onBack: () -> Unit,
    viewModel: NuovoClienteViewModel = hiltViewModel(),
) {
    val tokens = Freela.tokens
    var nome by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fonte by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(tokens.bg).verticalScroll(rememberScrollState()),
    ) {
        // Top bar: back + Annulla
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).border(1.dp, tokens.line, CircleShape).clickable { onBack() }.padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = null, tint = tokens.ink, modifier = Modifier.size(18.dp))
            }
            Text(
                stringResource(R.string.nc_annulla),
                color = tokens.muted,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onBack() }.padding(6.dp),
            )
        }

        // Avatar + anteprima nome
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Avatar(name = nome.ifBlank { "?" }, size = 72.dp)
            Text(
                nome.ifBlank { stringResource(R.string.nc_nome_placeholder) },
                color = if (nome.isBlank()) tokens.faint else tokens.ink,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(stringResource(R.string.nc_subtitle), color = tokens.muted, fontSize = 13.sp, textAlign = TextAlign.Center)
        }

        Column(modifier = Modifier.padding(horizontal = 22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            FormField(
                label = stringResource(R.string.nc_nome_label),
                value = nome,
                onValueChange = { nome = it },
                placeholder = stringResource(R.string.nc_nome_placeholder),
                imeAction = ImeAction.Next,
            )
            FormField(
                label = stringResource(R.string.nc_telefono_label),
                value = telefono,
                onValueChange = { telefono = it },
                placeholder = "+39 ___ ___ ____",
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next,
            )
            FormField(
                label = stringResource(R.string.nc_email_label),
                value = email,
                onValueChange = { email = it },
                placeholder = stringResource(R.string.nc_email_placeholder),
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            )
            FormField(
                label = stringResource(R.string.nc_fonte_label),
                value = fonte,
                onValueChange = { fonte = it },
                placeholder = stringResource(R.string.nc_fonte_placeholder),
                imeAction = ImeAction.Next,
            )
            FormField(
                label = stringResource(R.string.nc_tags_label),
                value = tags,
                onValueChange = { tags = it },
                placeholder = stringResource(R.string.nc_tags_placeholder),
                imeAction = ImeAction.Next,
            )
            FormField(
                label = stringResource(R.string.nc_note_label),
                value = note,
                onValueChange = { note = it },
                placeholder = stringResource(R.string.nc_note_placeholder),
                imeAction = ImeAction.Done,
            )
            Spacer(Modifier.height(8.dp))
            FreelaButton(
                text = stringResource(R.string.nc_salva),
                onClick = { viewModel.salva(nome, telefono, email, fonte, note, tags) { onBack() } },
                size = FreelaButtonSize.Large,
                fillMaxWidth = true,
                enabled = nome.isNotBlank(),
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
) {
    val tokens = Freela.tokens
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label.uppercase(), color = tokens.muted, style = tokens.typeExtras.monoCap)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(tokens.surface)
                .border(1.dp, tokens.line, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            if (value.isEmpty()) {
                Text(placeholder, color = tokens.faint, fontSize = 15.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(color = tokens.ink, fontSize = 15.sp),
                cursorBrush = SolidColor(tokens.accentBase),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
