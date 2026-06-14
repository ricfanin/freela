package com.freela.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.freela.app.domain.model.FasePipeline

// token fuori dallo standard md3, si leggono via LocalFreelaTokens dentro FreelaTheme
@Immutable
data class FreelaTokens(
    val bg: Color,
    val surface: Color,
    val surfaceLow: Color,
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
)

internal val LightFreelaTokens = FreelaTokens(
    bg = LightBg,
    surface = LightSurface,
    surfaceLow = LightSurfaceLow,
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
)

internal val DarkFreelaTokens = FreelaTokens(
    bg = DarkBg,
    surface = DarkSurface,
    surfaceLow = DarkSurfaceLow,
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
)

val LocalFreelaTokens = compositionLocalOf { LightFreelaTokens }

object Freela {
    val tokens: FreelaTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalFreelaTokens.current
}

// colore per ogni fase, usato da pipeline, chip stage e dot lista clienti
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
