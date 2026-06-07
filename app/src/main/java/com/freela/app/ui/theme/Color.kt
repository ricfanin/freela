package com.freela.app.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================
// Stile C "Living" — accent Forest (h=155°)
// Neutri tinti con la tinta dell'accento (effetto "Living").
// Valori da conversione oklch → sRGB (stesse formule L/C del mockup).
// =========================================================

// ---- Light ----
val LightBg          = Color(0xFFF1F9F3) // oklch(97.5% 0.012 155)
val LightSurface     = Color(0xFFFFFFFF)
val LightSurfaceLow  = Color(0xFFE9F3EC) // oklch(95.5% 0.015 155)
val LightSurfaceHi   = Color(0xFFFFFFFF)
val LightTonalBand   = Color(0xFFD4F0DC) // oklch(93% 0.04 155)
val LightInk         = Color(0xFF0D130F) // oklch(18% 0.014 155)
val LightMuted       = Color(0xFF566159) // oklch(48% 0.018 155)
val LightFaint       = Color(0xFF95A299) // oklch(70% 0.02 155)
val LightLine        = Color(0xFFDCE3DE) // oklch(91% 0.01 155)
val LightLineSoft    = Color(0xFFE7EDE9) // oklch(94% 0.008 155)
val LightChipBg      = Color(0xFFDFF0E4) // oklch(94% 0.025 155)

val LightAccentBase  = Color(0xFF187C49) // oklch(52% 0.12 155)
val LightAccentSoft  = Color(0xFFDAF7E3) // oklch(95% 0.04 155)
val LightAccentSofter= Color(0xFFE9FAEE) // oklch(97% 0.025 155)
val LightAccentInk   = Color(0xFF003E20) // oklch(32% 0.08 155)

val LightSuccess     = Color(0xFF1F8B5A) // oklch(52% 0.14 155)
val LightWarning     = Color(0xFFB07A1A) // oklch(58% 0.14 70)
val LightDanger      = Color(0xFFD63A2C) // oklch(56% 0.20 25)

// ---- Dark ----
val DarkBg           = Color(0xFF030704) // oklch(12% 0.012 155)
val DarkSurface      = Color(0xFF0B110D) // oklch(17% 0.014 155)
val DarkSurfaceLow   = Color(0xFF060B07) // oklch(14% 0.013 155)
val DarkSurfaceHi    = Color(0xFF101813) // oklch(20% 0.016 155)
val DarkTonalBand    = Color(0xFF051B0E) // oklch(20% 0.04 155)
val DarkInk          = Color(0xFFEAF1EB) // oklch(95% 0.01 155)
val DarkMuted        = Color(0xFF95A299) // oklch(70% 0.02 155)
val DarkFaint        = Color(0xFF5B675E) // oklch(50% 0.02 155)
val DarkLine         = Color(0xFF242B26) // oklch(28% 0.012 155)
val DarkLineSoft     = Color(0xFF171C19) // oklch(22% 0.01 155)
val DarkChipBg       = Color(0xFF19251D) // oklch(25% 0.022 155)

val DarkAccentBase   = Color(0xFF59B47D) // oklch(70% 0.12 155)
val DarkAccentSoft   = Color(0xFF182F20) // oklch(28% 0.04 155)
val DarkAccentSofter = Color(0xFF111E15) // oklch(22% 0.025 155)
val DarkAccentInk    = Color(0xFFAEE8C1) // oklch(88% 0.08 155)

val DarkSuccess      = Color(0xFF7CC79B) // oklch(74% 0.14 155)
val DarkWarning      = Color(0xFFD9A357) // oklch(78% 0.14 70)
val DarkDanger       = Color(0xFFE57A6F) // oklch(72% 0.18 25)

// =========================================================
// Pipeline stage colors (10 fasi, PRD FR-05)
// =========================================================
val StageNuovoLead          = LightFaint                  // neutral
val StagePrimoContatto      = Color(0xFF6E78C5)           // oklch(60% 0.10 260)
val StagePreventivoInviato  = Color(0xFFB68534)           // oklch(60% 0.13 60)
val StageInTrattativa       = Color(0xFFC07D58)           // oklch(60% 0.13 35)
val StageConfermato         = Color(0xFF3E8E5E)           // oklch(55% 0.13 145)
val StageInCorso            = LightAccentBase
val StageConsegnato         = Color(0xFF3D86A9)           // oklch(55% 0.13 200)
val StageInAttesaPagamento  = LightWarning
val StageChiuso             = LightFaint
val StageClienteRicorrente  = Color(0xFF9359AE)           // oklch(55% 0.13 295)
