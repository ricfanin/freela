package com.freela.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.freela.app.domain.model.FasePipeline

/**
 * Token "fuori MD3" del Design System Freela Stile C "Living".
 * Acceduti via [LocalFreelaTokens] dentro [FreelaTheme].
 */
@Immutable
data class FreelaTokens(
    val bg: Color,
    val surface: Color,
    val surfaceLow: Color,
    val surfaceHi: Color,
    val tonalBand: Color,
    val ink: Color,
    val muted: Color,
    val faint: Color,
    val line: Color,
    val lineSoft: Color,
    val chipBg: Color,
    val accentBase: Color,
    val accentSoft: Color,
    val accentSofter: Color,
    val accentInk: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val typeExtras: FreelaTypeExtras,
    val spacing: FreelaSpacing,
)

@Immutable
data class FreelaSpacing(
    val screenPadCompact: Dp = 16.dp,
    val screenPad: Dp = 22.dp,
    val screenPadSpacious: Dp = 26.dp,
    val cardPad: Dp = 16.dp,
    val cardPadSpacious: Dp = 22.dp,
    val gap: Dp = 12.dp,
    val gapSmall: Dp = 8.dp,
    val gapLarge: Dp = 18.dp,
)

internal val LightFreelaTokens = FreelaTokens(
    bg = LightBg,
    surface = LightSurface,
    surfaceLow = LightSurfaceLow,
    surfaceHi = LightSurfaceHi,
    tonalBand = LightTonalBand,
    ink = LightInk,
    muted = LightMuted,
    faint = LightFaint,
    line = LightLine,
    lineSoft = LightLineSoft,
    chipBg = LightChipBg,
    accentBase = LightAccentBase,
    accentSoft = LightAccentSoft,
    accentSofter = LightAccentSofter,
    accentInk = LightAccentInk,
    success = LightSuccess,
    warning = LightWarning,
    danger = LightDanger,
    typeExtras = DefaultFreelaTypeExtras,
    spacing = FreelaSpacing(),
)

internal val DarkFreelaTokens = FreelaTokens(
    bg = DarkBg,
    surface = DarkSurface,
    surfaceLow = DarkSurfaceLow,
    surfaceHi = DarkSurfaceHi,
    tonalBand = DarkTonalBand,
    ink = DarkInk,
    muted = DarkMuted,
    faint = DarkFaint,
    line = DarkLine,
    lineSoft = DarkLineSoft,
    chipBg = DarkChipBg,
    accentBase = DarkAccentBase,
    accentSoft = DarkAccentSoft,
    accentSofter = DarkAccentSofter,
    accentInk = DarkAccentInk,
    success = DarkSuccess,
    warning = DarkWarning,
    danger = DarkDanger,
    typeExtras = DefaultFreelaTypeExtras,
    spacing = FreelaSpacing(),
)

val LocalFreelaTokens = compositionLocalOf { LightFreelaTokens }

object Freela {
    val tokens: FreelaTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalFreelaTokens.current
}

/**
 * Stage color map (PRD FR-05). Usato da pipeline kanban, chip stage, dot lista clienti.
 */
@Composable
@ReadOnlyComposable
fun stageColor(stage: FasePipeline): Color = when (stage) {
    FasePipeline.NUOVO_LEAD -> StageNuovoLead
    FasePipeline.PRIMO_CONTATTO -> StagePrimoContatto
    FasePipeline.PREVENTIVO_INVIATO -> StagePreventivoInviato
    FasePipeline.IN_TRATTATIVA -> StageInTrattativa
    FasePipeline.CONFERMATO -> StageConfermato
    FasePipeline.IN_CORSO -> Freela.tokens.accentBase
    FasePipeline.CONSEGNATO -> StageConsegnato
    FasePipeline.IN_ATTESA_PAGAMENTO -> Freela.tokens.warning
    FasePipeline.CHIUSO -> Freela.tokens.faint
    FasePipeline.CLIENTE_RICORRENTE -> StageClienteRicorrente
}
