package com.example.yutorikaizen.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.sp
import com.example.yutorikaizen.R

// ─── Font families ─────────────────────────────────────────────────────────────
//
//  DM Sans  — Geometric sans-serif. Warm, human, never clinical.
//             Body text, buttons, labels, UI chrome.
//
//  Fraunces — Variable optical serif with real character.
//             For titles and numbers that need soul. Feels handcrafted.

val DmSans = FontFamily(
    Font(R.font.dm_sans, FontWeight.Normal),
    Font(R.font.dm_sans, FontWeight.Medium),
    Font(R.font.dm_sans, FontWeight.SemiBold),
    Font(R.font.dm_sans, FontWeight.Bold),
)

val Fraunces = FontFamily(
    Font(R.font.fraunces, FontWeight.Normal),
    Font(R.font.fraunces, FontWeight.Medium),
    Font(R.font.fraunces, FontWeight.SemiBold),
    Font(R.font.fraunces, FontWeight.Bold),
)

/**
 * Caveat — handwriting style. Use for:
 *  • Greetings ("Good morning")
 *  • Phase names on the journey screen
 *  • The surge bridge headline
 *  • Any single line that needs to feel personal, not product-y
 *
 * Do NOT use for paragraphs, labels, or anything below 20sp.
 */
val Caveat = FontFamily(
    Font(R.font.caveat, FontWeight.Normal),
    Font(R.font.caveat, FontWeight.SemiBold),
    Font(R.font.caveat, FontWeight.Bold),
)

// Keep old aliases so nothing else breaks
val NunitoFamily       = DmSans
val PressStart2PFamily = Fraunces


// ─── Typography scale ──────────────────────────────────────────────────────────
val AppTypography = Typography(

    // Big titles — Fraunces gives real presence
    displayLarge = TextStyle(
        fontFamily    = Fraunces,
        fontWeight    = FontWeight.Bold,
        fontSize      = 40.sp,
        lineHeight    = 46.sp,
        letterSpacing = (-0.5).sp,
        color         = StarWhite
    ),
    displayMedium = TextStyle(
        fontFamily    = Fraunces,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 32.sp,
        lineHeight    = 38.sp,
        letterSpacing = (-0.3).sp,
        color         = StarWhite
    ),

    // Screen headings
    headlineLarge = TextStyle(
        fontFamily    = Fraunces,
        fontWeight    = FontWeight.Bold,
        fontSize      = 26.sp,
        lineHeight    = 32.sp,
        letterSpacing = (-0.2).sp,
        color         = StarWhite
    ),
    headlineMedium = TextStyle(
        fontFamily    = Fraunces,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 21.sp,
        lineHeight    = 27.sp,
        letterSpacing = (-0.1).sp,
        color         = StarWhite
    ),
    headlineSmall = TextStyle(
        fontFamily    = DmSans,
        fontWeight    = FontWeight.Bold,
        fontSize      = 18.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.sp,
        color         = StarWhite
    ),

    // Card and section titles
    titleLarge = TextStyle(
        fontFamily    = Fraunces,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 17.sp,
        lineHeight    = 23.sp,
        letterSpacing = 0.sp,
        color         = StarWhite
    ),
    titleMedium = TextStyle(
        fontFamily    = Fraunces,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 15.sp,
        lineHeight    = 21.sp,
        letterSpacing = 0.sp,
        color         = StarWhite
    ),
    titleSmall = TextStyle(
        fontFamily    = Fraunces,
        fontWeight    = FontWeight.Medium,
        fontSize      = 13.sp,
        lineHeight    = 18.sp,
        letterSpacing = 0.sp,
        color         = StarWhite
    ),

    // Reading text
    bodyLarge = TextStyle(
        fontFamily    = DmSans,
        fontWeight    = FontWeight.Normal,
        fontSize      = 16.sp,
        lineHeight    = 25.sp,
        letterSpacing = 0.sp,
        color         = StarWhite
    ),
    bodyMedium = TextStyle(
        fontFamily    = DmSans,
        fontWeight    = FontWeight.Normal,
        fontSize      = 14.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.sp,
        color         = MoonGray
    ),
    bodySmall = TextStyle(
        fontFamily    = DmSans,
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        lineHeight    = 18.sp,
        letterSpacing = 0.sp,
        color         = MoonGray
    ),

    // Buttons, chips, nav labels
    labelLarge = TextStyle(
        fontFamily    = DmSans,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 15.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.sp,
        color         = StarWhite
    ),
    labelMedium = TextStyle(
        fontFamily    = DmSans,
        fontWeight    = FontWeight.Medium,
        fontSize      = 13.sp,
        lineHeight    = 18.sp,
        letterSpacing = 0.sp,
        color         = StarWhite
    ),
    labelSmall = TextStyle(
        fontFamily    = DmSans,
        fontWeight    = FontWeight.Medium,
        fontSize      = 11.sp,
        lineHeight    = 15.sp,
        letterSpacing = 0.3.sp,
        color         = MoonGray
    )
)
