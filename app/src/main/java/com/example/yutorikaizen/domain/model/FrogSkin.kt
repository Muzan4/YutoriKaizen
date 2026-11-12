package com.example.yutorikaizen.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Represents a frog skin option.
 *
 * [id]          — matches the `.tres` filename (without extension) inside the `.pck`.
 *                 This exact string is passed to Godot via `--skin <id>` in getCommandLine().
 * [displayName] — shown in the skin picker UI.
 * [emoji]       — visual preview in the skin card (no image assets needed).
 * [description] — flavour text shown under the card.
 * [accentColor] — glow / border colour for the card when selected.
 */
data class FrogSkin(
    val id: String,
    val displayName: String,
    val emoji: String,
    val description: String,
    val accentColor: Color
)

/** All available skins, ordered so the default (green) is first. */
val ALL_SKINS = listOf(
    FrogSkin(
        id          = "green",
        displayName = "Classic",
        emoji       = "🐸",
        description = "The original green friend",
        accentColor = Color(0xFF7CB47A)   // MossGreen
    ),
    FrogSkin(
        id          = "blue",
        displayName = "Ocean",
        emoji       = "🫐",
        description = "Cool ocean-blue vibes",
        accentColor = Color(0xFF5B9BD5)
    ),
    FrogSkin(
        id          = "purple",
        displayName = "Mystic",
        emoji       = "🔮",
        description = "Mysterious purple aura",
        accentColor = Color(0xFF9B59B6)
    ),
    FrogSkin(
        id          = "baw",
        displayName = "Monochrome",
        emoji       = "🤍",
        description = "Classic black & white",
        accentColor = Color(0xFFBDBDBD)
    ),
    FrogSkin(
        id          = "brown",
        displayName = "Earthy",
        emoji       = "🍂",
        description = "Warm earthy brown tones",
        accentColor = Color(0xFFA0522D)
    ),
    FrogSkin(
        id          = "clown",
        displayName = "Clown",
        emoji       = "🤡",
        description = "For the chaotic ones",
        accentColor = Color(0xFFE74C3C)
    ),
    FrogSkin(
        id          = "cowboy",
        displayName = "Cowboy",
        emoji       = "🤠",
        description = "Yeehaw, partner 🐸",
        accentColor = Color(0xFFD4956A)   // CozyAmber
    ),
    FrogSkin(
        id          = "funny",
        displayName = "Funny",
        emoji       = "😂",
        description = "Extra goofy, maximum chaos",
        accentColor = Color(0xFFF1C40F)
    ),
    FrogSkin(
        id          = "hat",
        displayName = "Hat",
        emoji       = "🎩",
        description = "Dapper frog with style",
        accentColor = Color(0xFF2ECC71)
    ),
    FrogSkin(
        id          = "tanp",
        displayName = "Tropical",
        emoji       = "🌴",
        description = "Sun-kissed tan vibes",
        accentColor = Color(0xFFE67E22)
    ),
    FrogSkin(
        id          = "viking",
        displayName = "Viking",
        emoji       = "⚔️",
        description = "Raids and ribbit",
        accentColor = Color(0xFF3498DB)
    )
)
