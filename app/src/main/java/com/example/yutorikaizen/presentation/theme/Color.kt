package com.example.yutorikaizen.presentation.theme

import androidx.compose.ui.graphics.Color

// ==========================================================
// THEME: "Calm Midnight Pond"
// Philosophy: warm-tinted dark (never cold), one glowing teal
// accent that reads like still water, duck amber for warmth.
// ==========================================================

// === Backgrounds ===
val DeepNight      = Color(0xFF14111E)   // Main background — warm purple-black, not cold
val NightSurface   = Color(0xFF1D1929)   // Bottom nav, bottom sheets, nav bar
val NightCard      = Color(0xFF26233A)   // Cards, elevated surfaces
val NightBorder    = Color(0xFF332F45)   // Subtle dividers, outlines
val NightHighlight = Color(0xFF3A3555)   // Pressed/selected chip, hover state

// === Primary — Calm Teal ===
// WHY teal, not green? Pure green reads as clinical/productivity-app.
// This #4ECDC4 sits between aqua and teal — it evokes calm water at night,
// not a traffic light or a stock chart.
val CalmTeal       = Color(0xFF4ECDC4)   // Primary buttons, active states, ring end
val CalmTealDim    = Color(0xFF2A8C85)   // Pressed state, darker teal
val TealContainer  = Color(0xFF0F3835)   // Button container background (dark teal tint)
val TealGlow       = Color(0x334ECDC4)   // 20% teal glow for soft halos

// === Secondary — Duck Amber ===
// The duck is the only warm thing on screen. Amber against deep purple
// creates a cozy campfire-at-night contrast — intentional and distinctive.
val DuckAmber      = Color(0xFFE8A830)   // Duck body, Duck Break button, highlights
val DuckAmberDim   = Color(0xFFC4861C)   // Pressed amber, darker variant
val DuckAmberLight = Color(0xFFF5C842)   // Bright accent, selected state
val AmberContainer = Color(0xFF3D2A00)   // Amber button container background
val AmberGlow      = Color(0x33E8A830)   // 20% amber glow
val DuckAmberDark  = DuckAmberDim        // Alias for Canvas drawing code

// === Consistency Ring Gradient ===
// Low score → purple (gentle, not alarming), High score → teal (calm achievement)
val RingPurple     = Color(0xFFA78BFA)   // Ring start color (low score end)
val RingTeal       = Color(0xFF4ECDC4)   // Ring end color (high score end)
val RingTrack      = Color(0xFF2A263F)   // Empty ring track background

// === Text ===
// "Star White" has a very slight lavender tint (#EDE9F8) so it blends
// with the purple backgrounds instead of clashing like pure white would.
val StarWhite      = Color(0xFFEDE9F8)   // Primary text
val MoonGray       = Color(0xFF9B96AD)   // Secondary text, captions, hints
val DuskFaint      = Color(0xFF5C5870)   // Disabled, placeholder

// === Semantic — warm alternatives to red/green ===
val SoftMint       = Color(0xFF5BE5A0)   // Completed session/task — mint green, not harsh
val WarmMiss       = Color(0xFFC4861C)   // Missed day — amber, never red
val WarmMissContainer = Color(0xFF2E1E00) // Missed day chip background

// Aliases so that other files (Canvas code, legacy refs) keep compiling
val InkBrown       = StarWhite
val InkLight       = MoonGray
val SageGreen      = CalmTeal
val SageGreenLight = RingTrack
val SageGreenDark  = CalmTealDim
