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
private val MidnightPondScheme = darkColorScheme(
    primary               = CalmTeal,
    onPrimary             = DeepNight,          // dark text on teal button
    primaryContainer      = TealContainer,
    onPrimaryContainer    = CalmTeal,
    secondary             = DuckAmber,
    onSecondary           = DeepNight,
    secondaryContainer    = AmberContainer,
    onSecondaryContainer  = DuckAmberLight,
    background            = DeepNight,
    onBackground          = StarWhite,
    surface               = NightSurface,
    onSurface             = StarWhite,
    surfaceVariant        = NightCard,
    onSurfaceVariant      = MoonGray,
    outline               = NightBorder,
    outlineVariant        = NightHighlight,
    error                 = WarmMiss,           // warm amber — never red
    onError               = DeepNight,
    errorContainer        = WarmMissContainer,
    onErrorContainer      = DuckAmberLight,
    inverseSurface        = StarWhite,
    inverseOnSurface      = DeepNight,
    inversePrimary        = CalmTealDim,
    scrim                 = Color(0xCC14111E)    // 80% DeepNight — modal backdrop
)

@Composable
fun YutoriKaizenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MidnightPondScheme,
        typography  = AppTypography,
        content     = content
    )
}
