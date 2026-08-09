package com.example.yutorikaizen.presentation.theme

import androidx.compose.ui.graphics.Color

// ==========================================================
// THEME: "Cozy Midnight Frog Pond"
// Philosophy: deep matte blacks as the base, warm earthy
// accents (moss green, warm cream) for a cozy cottage feel.
// Never cold, never clinical.
// ==========================================================

// === Core Blacks ===
val CozyBlack      = Color(0xFF0D0D0D)   // True matte black — base background
val CozyBlack2     = Color(0xFF141414)   // Slightly lifted surface
val CozyBlack3     = Color(0xFF1C1C1C)   // Cards, nav bar
val CozyBlack4     = Color(0xFF242424)   // Elevated cards, modals
val CozyBorder     = Color(0xFF2E2E2E)   // Subtle dividers, outlines

// === Warm Creams & Tans ===
val WarmCream      = Color(0xFFE8DCC8)   // Primary text — warm parchment, not pure white
val CreamWhite     = Color(0xFFF2EBD9)   // Bright text, headings
val SoftTan        = Color(0xFFB8A898)   // Secondary text, captions
val DimTan         = Color(0xFF6E5E50)   // Disabled, placeholder

// === Accent: Moss Green ===
// Why moss green? It matches the frog without being neon.
// Earthy, cozy, reminiscent of forest ponds at dusk.
val MossGreen      = Color(0xFF7CB47A)   // Primary accent — buttons, active icons
val MossGreenDark  = Color(0xFF4F7A4D)   // Pressed state, darker variant
val MossGreenDim   = Color(0xFF1E3D1C)   // Container background, chip selected
val MossGlow       = Color(0x337CB47A)   // 20% glow for breathing circles

// === Accent: Warm Amber (secondary) ===
val CozyAmber      = Color(0xFFD4956A)   // Warm accent for highlights, frog break
val CozyAmberDim   = Color(0xFF8C5A35)   // Pressed amber
val CozyAmberGlow  = Color(0x33D4956A)   // 20% amber glow

// === Semantic ===
val SoftSage       = Color(0xFF9DC49A)   // Completed / success — gentle, not harsh green
val WarmWarn       = Color(0xFFD4956A)   // Warning — amber, never red

// === Legacy aliases (keep other files from breaking) ===
val DeepNight      = CozyBlack
val NightSurface   = CozyBlack3
val NightCard      = CozyBlack4
val NightBorder    = CozyBorder
val NightHighlight = CozyBlack4
val CalmTeal       = MossGreen
val CalmTealDim    = MossGreenDark
val TealContainer  = MossGreenDim
val TealGlow       = MossGlow
val DuckAmber      = CozyAmber
val DuckAmberDim   = CozyAmberDim
val DuckAmberLight = CozyAmber
val AmberContainer = Color(0xFF2A1A0A)
val AmberGlow      = CozyAmberGlow
val DuckAmberDark  = CozyAmberDim
val RingPurple     = MossGreen
val RingTeal       = SoftSage
val RingTrack      = CozyBlack3
val StarWhite      = CreamWhite
val MoonGray       = SoftTan
val DuskFaint      = DimTan
val SoftMint       = SoftSage
val WarmMiss       = WarmWarn
val WarmMissContainer = Color(0xFF2A1400)
val InkBrown       = CreamWhite
val InkLight       = SoftTan
val SageGreen      = MossGreen
val SageGreenLight = MossGreenDim
val SageGreenDark  = MossGreenDark
