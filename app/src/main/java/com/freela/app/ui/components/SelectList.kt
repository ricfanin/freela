package com.freela.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freela.app.ui.theme.Freela

// id nullable per gestire scelte tipo "nessun progetto"
data class SelectOption(
    val id: Long?,
    val label: String,
    val avatar: Boolean = false,
)

// righe piene con target di tocco ampi per i dialog di scelta cliente/progetto, al posto delle chip piccole
@Composable
fun SelectList(
    options: List<SelectOption>,
    selectedId: Long?,
    onPick: (Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tokens = Freela.tokens
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { opt ->
            val selected = opt.id == selectedId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selected) tokens.accentSofter else Color.Transparent)
                    .clickable { onPick(opt.id) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (opt.avatar) Avatar(name = opt.label, size = 34.dp)
                Text(
                    opt.label,
                    color = if (selected) tokens.accentInk else tokens.ink,
                    fontSize = 16.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                if (selected) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = null,
                        tint = tokens.accentBase,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}
