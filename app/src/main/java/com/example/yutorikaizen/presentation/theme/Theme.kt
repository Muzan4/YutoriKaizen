package com.example.yutorikaizen.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// WHY darkColorScheme from the start — not a light theme with a toggle?
// The app is dark-first. Building on darkColorScheme means every Material3
// component (dialogs, chips, bottom sheets, snackbars) inherits the palette
// automatically without per-component overrides. The "scrim" color for modals
// is also handled here so we never get a jarring white flash.
//
// Color role decisions:
//   primary       = CalmTeal    — buttons, FAB, active nav icon
//   secondary     = DuckAmber   — Duck Break, duck highlights
//   background    = DeepNight   — root scaffold background
//   surface       = NightSurface — cards, bottom nav, sheets
//   error         = WarmMiss    — NEVER red — warm amber for missed/warning states
private val CozyMidnightScheme = darkColorScheme(
    primary               = MossGreen,
    onPrimary             = CozyBlack,
    primaryContainer      = MossGreenDim,
    onPrimaryContainer    = MossGreen,
    secondary             = CozyAmber,
    onSecondary           = CozyBlack,
    secondaryContainer    = AmberContainer,
    onSecondaryContainer  = CozyAmber,
    background            = CozyBlack,
    onBackground          = CreamWhite,
    surface               = CozyBlack3,
    onSurface             = CreamWhite,
    surfaceVariant        = CozyBlack4,
    onSurfaceVariant      = SoftTan,
    outline               = CozyBorder,
    outlineVariant        = CozyBlack4,
    error                 = WarmWarn,
    onError               = CozyBlack,
    errorContainer        = WarmMissContainer,
    onErrorContainer      = CozyAmber,
    inverseSurface        = CreamWhite,
    inverseOnSurface      = CozyBlack,
    inversePrimary        = MossGreenDark,
    scrim                 = Color(0xCC0D0D0D)
)

@Composable
fun YutoriKaizenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CozyMidnightScheme,
        typography  = AppTypography,
        content     = content
    )
}
