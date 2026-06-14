package com.freela.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.freela.app.ui.theme.Manrope

// iniziali del nome su un gradient con hue derivato dall'hash del nome, così ogni cliente ha un colore stabile e suo
@Composable
fun Avatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    overrideColor: Color? = null,
) {
    val initials = remember(name) { initialsOf(name) }
    val brush = remember(name, overrideColor) {
        if (overrideColor != null) {
            Brush.linearGradient(listOf(overrideColor, overrideColor))
        } else {
            val hue = hashHue(name)
            Brush.linearGradient(
                colors = listOf(
                    oklchToColor(0.78f, 0.10f, hue),
                    oklchToColor(0.62f, 0.14f, (hue + 60f) % 360f),
                ),
            )
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(brush),
        contentAlignment = Alignment.Center,
    ) {
        if (size >= 18.dp) {
            Text(
                text = initials,
                color = Color.White,
                style = TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = (size.value * 0.36f).sp,
                    letterSpacing = (-0.02).sp,
                ),
            )
        }
    }
}

private fun initialsOf(name: String): String =
    name.trim().split(Regex("\\s+"))
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

private fun hashHue(s: String): Float {
    var h = 0
    for (ch in s) h = (h * 31 + ch.code) % 360
    return ((h + 360) % 360).toFloat()
}

// conversione oklch -> sRGB (formula di björn ottosson)
fun oklchToColor(L: Float, C: Float, hDeg: Float): Color {
    val hRad = Math.toRadians(hDeg.toDouble()).toFloat()
    val a = (C * kotlin.math.cos(hRad))
    val b = (C * kotlin.math.sin(hRad))

    val l_ = (L + 0.3963377774f * a + 0.2158037573f * b).toDouble()
    val m_ = (L - 0.1055613458f * a - 0.0638541728f * b).toDouble()
    val s_ = (L - 0.0894841775f * a - 1.2914855480f * b).toDouble()

    val l3 = l_ * l_ * l_
    val m3 = m_ * m_ * m_
    val s3 = s_ * s_ * s_

    var r = (+4.0767416621f * l3 - 3.3077115913f * m3 + 0.2309699292f * s3).toFloat()
    var g = (-1.2684380046f * l3 + 2.6097574011f * m3 - 0.3413193965f * s3).toFloat()
    var b2 = (-0.0041960863f * l3 - 0.7034186147f * m3 + 1.7076147010f * s3).toFloat()

    r = linearToSrgb(r)
    g = linearToSrgb(g)
    b2 = linearToSrgb(b2)

    return Color(
        red = r.coerceIn(0f, 1f),
        green = g.coerceIn(0f, 1f),
        blue = b2.coerceIn(0f, 1f),
    )
}

private fun linearToSrgb(v: Float): Float =
    if (v <= 0.0031308f) 12.92f * v
    else 1.055f * v.pow(1f / 2.4f) - 0.055f

private fun Float.pow(p: Float): Float = Math.pow(this.toDouble(), p.toDouble()).toFloat()
