package com.example.yutorikaizen.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ==========================================================
// FONTS — Phase 0 uses system fonts so the app builds immediately.
//
// To upgrade to real fonts (Phase 1+):
//  1. Download from Google Fonts:
//     - Nunito (Regular, SemiBold, Bold) → res/font/nunito_*.ttf
//     - Press Start 2P (Regular)         → res/font/press_start_2p.ttf
//  2. Uncomment the FontFamily declarations below and comment
//     out the Default aliases.
//  3. Re-run assembleDebug — no other files need to change.
// ==========================================================

// --- Uncomment after adding font files to res/font/ ---
// val NunitoFamily = FontFamily(
//     Font(R.font.nunito_regular, FontWeight.Normal),
//     Font(R.font.nunito_semibold, FontWeight.SemiBold),
//     Font(R.font.nunito_bold, FontWeight.Bold)
// )
// val PressStart2PFamily = FontFamily(
//     Font(R.font.press_start_2p, FontWeight.Normal)
// )

// Phase 0 placeholders — system sans-serif
val NunitoFamily       = FontFamily.Default
val PressStart2PFamily = FontFamily.Monospace   // closest system font vibe for now

// WHY: We use Nunito for all body/UI text because its rounded letterforms match
// the cozy aesthetic without sacrificing readability. Press Start 2P is only for
// the duck's speech bubble tips, score numbers, and phase headers — sparingly.
// On dark backgrounds: default text color is StarWhite (lavender-tinted off-white).
val AppTypography = Typography(
    // Display: duck speech bubble headers, score numbers (pixel font)
    displayLarge = TextStyle(
        fontFamily = PressStart2PFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        color = StarWhite
    ),
    displayMedium = TextStyle(
        fontFamily = PressStart2PFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = StarWhite
    ),
    // Headings: screen titles
    headlineLarge = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        color = StarWhite
    ),
    headlineMedium = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        color = StarWhite
    ),
    // Body: all reading text
    bodyLarge = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = StarWhite
    ),
    bodyMedium = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = MoonGray
    ),
    // Labels: buttons, chips, nav
    labelLarge = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = StarWhite
    ),
    labelSmall = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = MoonGray
    )
)
