package com.freela.app.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================
// Stile C "Living" — accent Indigo (h=265°)
// Valori dal design_handoff_freela/README.md §Design Tokens.
// Approssimazioni oklch → sRGB già fornite dal README.
// =========================================================

// ---- Light ----
val LightBg          = Color(0xFFF4F5FB) // oklch(97.5% 0.012 265)
val LightSurface     = Color(0xFFFFFFFF)
val LightSurfaceLow  = Color(0xFFEDEFF7) // oklch(95.5% 0.015 265)
val LightSurfaceHi   = Color(0xFFFFFFFF)
val LightTonalBand   = Color(0xFFE0E5F5) // oklch(93% 0.04 265)
val LightInk         = Color(0xFF1B1D29) // oklch(18% 0.014 265)
val LightMuted       = Color(0xFF6A6F82) // oklch(48% 0.018 265)
val LightFaint       = Color(0xFFA3A8B8) // oklch(70% 0.02 265)
val LightLine        = Color(0xFFDCDFE7) // oklch(91% 0.01 265)
val LightLineSoft    = Color(0xFFE8EAEF) // oklch(94% 0.008 265)
val LightChipBg      = Color(0xFFE5E8F2) // oklch(94% 0.025 265)

val LightAccentBase  = Color(0xFF4C53C9) // oklch(52% 0.17 265)
val LightAccentSoft  = Color(0xFFE5E7F8) // oklch(95% 0.04 265)
val LightAccentSofter= Color(0xFFF2F3FA) // oklch(97% 0.025 265)
val LightAccentInk   = Color(0xFF353B8F) // oklch(32% 0.08 265)

val LightSuccess     = Color(0xFF1F8B5A) // oklch(52% 0.14 155)
val LightWarning     = Color(0xFFB07A1A) // oklch(58% 0.14 70)
val LightDanger      = Color(0xFFD63A2C) // oklch(56% 0.20 25)

// ---- Dark ----
val DarkBg           = Color(0xFF0D0E14) // oklch(12% 0.012 265)
val DarkSurface      = Color(0xFF16181F) // oklch(17% 0.014 265)
val DarkSurfaceLow   = Color(0xFF12141A) // oklch(14% 0.013 265)
val DarkSurfaceHi    = Color(0xFF1B1E26) // oklch(20% 0.016 265)
val DarkTonalBand    = Color(0xFF1F2330) // oklch(20% 0.04 265)
val DarkInk          = Color(0xFFEFF0F4) // oklch(95% 0.01 265)
val DarkMuted        = Color(0xFFA9ADBA) // oklch(70% 0.02 265)
val DarkFaint        = Color(0xFF747788) // oklch(50% 0.02 265)
val DarkLine         = Color(0xFF2A2D36) // oklch(28% 0.012 265)
val DarkLineSoft     = Color(0xFF1F222A) // oklch(22% 0.01 265)
val DarkChipBg       = Color(0xFF272A35) // oklch(25% 0.022 265)

val DarkAccentBase   = Color(0xFF8C92E0) // oklch(70% 0.17 265)
val DarkAccentSoft   = Color(0xFF323657) // oklch(28% 0.04 265)
val DarkAccentSofter = Color(0xFF272A3F) // oklch(22% 0.025 265)
val DarkAccentInk    = Color(0xFFC8CBED) // oklch(88% 0.08 265)

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
