package com.freela.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font as GoogleFontFont
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.freela.app.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val manropeGoogle = GoogleFont("Manrope")
private val jetbrainsMonoGoogle = GoogleFont("JetBrains Mono")

val Manrope: FontFamily = FontFamily(
    GoogleFontFont(googleFont = manropeGoogle, fontProvider = provider, weight = FontWeight.Light),
    GoogleFontFont(googleFont = manropeGoogle, fontProvider = provider, weight = FontWeight.Normal),
    GoogleFontFont(googleFont = manropeGoogle, fontProvider = provider, weight = FontWeight.Medium),
    GoogleFontFont(googleFont = manropeGoogle, fontProvider = provider, weight = FontWeight.SemiBold),
    GoogleFontFont(googleFont = manropeGoogle, fontProvider = provider, weight = FontWeight.Bold),
)

val JetBrainsMono: FontFamily = FontFamily(
    GoogleFontFont(googleFont = jetbrainsMonoGoogle, fontProvider = provider, weight = FontWeight.Normal),
    GoogleFontFont(googleFont = jetbrainsMonoGoogle, fontProvider = provider, weight = FontWeight.Medium),
    GoogleFontFont(googleFont = jetbrainsMonoGoogle, fontProvider = provider, weight = FontWeight.SemiBold),
    GoogleFontFont(googleFont = jetbrainsMonoGoogle, fontProvider = provider, weight = FontWeight.Bold),
)

// fallback se al primo avvio i font non si scaricano offline
val ManropeWithFallback = Manrope
val MonoWithFallback = JetBrainsMono

private const val DEFAULT_TRACKING = -0.014f

val FreelaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.03).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 44.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.03).sp,
        fontFeatureSettings = "tnum",
    ),
    headlineMedium = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.025).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.025).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.02).sp,
    ),
    titleSmall = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.015).sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = DEFAULT_TRACKING.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = DEFAULT_TRACKING.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = DEFAULT_TRACKING.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = DEFAULT_TRACKING.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = DEFAULT_TRACKING.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = MonoWithFallback,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.10f.sp,
    ),
)

// stili tipografici extra fuori da md3
data class FreelaTypeExtras(
    val monoCap: TextStyle,       // label sezione/badge
    val numberStat: TextStyle,    // valore di StatTile/BigNumber
    val displayNum: TextStyle,    // hero numerica tipo Finanze
    val timerHero: TextStyle,     // timer della schermata tracking
    val monoMeta: TextStyle,      // timestamp, id fattura
)

val DefaultFreelaTypeExtras = FreelaTypeExtras(
    monoCap = TextStyle(
        fontFamily = MonoWithFallback,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 1.45.em,
        letterSpacing = 0.10f.sp,
        fontStyle = FontStyle.Normal,
    ),
    numberStat = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        letterSpacing = (-0.025).sp,
        lineHeight = 28.sp,
        fontFeatureSettings = "tnum",
    ),
    displayNum = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 44.sp,
        letterSpacing = (-0.03).sp,
        lineHeight = 48.sp,
        fontFeatureSettings = "tnum",
    ),
    timerHero = TextStyle(
        fontFamily = ManropeWithFallback,
        fontWeight = FontWeight.Light,
        fontSize = 78.sp,
        letterSpacing = (-0.045).sp,
        lineHeight = 80.sp,
        fontFeatureSettings = "tnum",
    ),
    monoMeta = TextStyle(
        fontFamily = MonoWithFallback,
        fontWeight = FontWeight.Medium,
        fontSize = 11.5f.sp,
        lineHeight = 1.4.em,
        letterSpacing = 0.sp,
        fontFeatureSettings = "tnum",
    ),
)
