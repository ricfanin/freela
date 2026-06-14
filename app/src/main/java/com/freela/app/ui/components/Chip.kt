package com.freela.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.PillShape

enum class ChipTone { Neutral, Accent, Success, Warning, Danger }
enum class ChipSize { Small, Medium }

@Composable
fun FreelaChip(
    text: String,
    modifier: Modifier = Modifier,
    tone: ChipTone = ChipTone.Neutral,
    size: ChipSize = ChipSize.Small,
    dot: Boolean = false,
) {
    val tokens = Freela.tokens
    val palette: ChipPalette = when (tone) {
        ChipTone.Neutral -> ChipPalette(bg = tokens.chipBg, fg = tokens.muted, dotc = tokens.faint, border = Color.Transparent)
        ChipTone.Accent  -> ChipPalette(bg = tokens.accentBase, fg = Color.White, dotc = Color.White, border = Color.Transparent)
        ChipTone.Success -> ChipPalette(bg = tokens.success, fg = Color.White, dotc = Color.White, border = Color.Transparent)
        ChipTone.Warning -> ChipPalette(bg = tokens.warning, fg = Color.White, dotc = Color.White, border = Color.Transparent)
        ChipTone.Danger  -> ChipPalette(bg = tokens.danger, fg = Color.White, dotc = Color.White, border = Color.Transparent)
    }

    val padding = if (size == ChipSize.Medium) PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                  else PaddingValues(horizontal = 9.dp, vertical = 3.dp)
    val fontSize = if (size == ChipSize.Medium) 13.sp else 11.5f.sp

    Row(
        modifier = modifier
            .clip(PillShape)
            .background(palette.bg)
            .border(width = 0.dp, color = palette.border, shape = PillShape)
            .padding(padding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (dot) {
            Box(Modifier.size(5.dp).clip(CircleShape).background(palette.dotc))
        }
        Text(
            text = text,
            color = palette.fg,
            style = TextStyle(
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.01f.sp,
                fontFamily = MaterialTheme.typography.bodyMedium.fontFamily,
            ),
        )
    }
}

private data class ChipPalette(val bg: Color, val fg: Color, val dotc: Color, val border: Color)
