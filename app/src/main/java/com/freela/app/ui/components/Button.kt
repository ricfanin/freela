package com.freela.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freela.app.ui.theme.Freela
import com.freela.app.ui.theme.Manrope
import com.freela.app.ui.theme.PillShape

enum class FreelaButtonVariant { Primary, Ghost, Soft, Danger }
enum class FreelaButtonSize { Small, Medium, Large }

@Composable
fun FreelaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: FreelaButtonVariant = FreelaButtonVariant.Primary,
    size: FreelaButtonSize = FreelaButtonSize.Medium,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = false,
) {
    val tokens = Freela.tokens
    val (height, padding, fontSize) = when (size) {
        FreelaButtonSize.Small  -> Triple(34.dp, PaddingValues(horizontal = 14.dp, vertical = 0.dp), 13.sp)
        FreelaButtonSize.Medium -> Triple(44.dp, PaddingValues(horizontal = 18.dp, vertical = 0.dp), 14.sp)
        FreelaButtonSize.Large  -> Triple(52.dp, PaddingValues(horizontal = 22.dp, vertical = 0.dp), 15.sp)
    }
    val baseMod = modifier
        .heightIn(min = height)
        .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)

    val textStyle = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize,
        letterSpacing = (-0.014f).sp,
    )

    val content: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(padding),
        ) {
            leading?.invoke()
            Text(text, style = textStyle)
            trailing?.invoke()
        }
    }

    when (variant) {
        FreelaButtonVariant.Primary -> Button(
            onClick = onClick,
            modifier = baseMod,
            enabled = enabled,
            shape = PillShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = tokens.accentBase,
                contentColor = Color.White,
            ),
        ) { content() }

        FreelaButtonVariant.Ghost -> OutlinedButton(
            onClick = onClick,
            modifier = baseMod,
            enabled = enabled,
            shape = PillShape,
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(1.dp, tokens.line),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = tokens.ink,
            ),
        ) { content() }

        FreelaButtonVariant.Soft -> Button(
            onClick = onClick,
            modifier = baseMod,
            enabled = enabled,
            shape = PillShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = tokens.chipBg,
                contentColor = tokens.ink,
            ),
        ) { content() }

        FreelaButtonVariant.Danger -> OutlinedButton(
            onClick = onClick,
            modifier = baseMod,
            enabled = enabled,
            shape = PillShape,
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(1.dp, tokens.danger.copy(alpha = 0.33f)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = tokens.danger,
            ),
        ) { content() }
    }
}
