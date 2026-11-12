package com.example.yutorikaizen.domain.model

object SocialMediaApps {
    val DEFAULT_PACKAGES = mapOf(
        "com.instagram.android" to "Instagram",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.google.android.youtube" to "YouTube",
        "com.twitter.android" to "X (Twitter)",
        "com.snapchat.android" to "Snapchat",
        "com.reddit.frontpage" to "Reddit",
        "com.facebook.katana" to "Facebook",
        "com.pinterest" to "Pinterest",
        "com.instagram.barcelona" to "Threads",
        "com.whatsapp" to "WhatsApp",
        "org.telegram.messenger" to "Telegram",
        "com.discord" to "Discord",
        "com.linkedin.android" to "LinkedIn"
    )
    
    fun isSocialMedia(packageName: String): Boolean = packageName in DEFAULT_PACKAGES
    
    fun getLabel(packageName: String): String = DEFAULT_PACKAGES[packageName] ?: packageName
    
    fun getEmoji(packageName: String): String = when (packageName) {
        "com.instagram.android" -> "📸"
        "com.zhiliaoapp.musically" -> "🎵"
        "com.google.android.youtube" -> "▶️"
        "com.twitter.android" -> "🐦"
        "com.snapchat.android" -> "👻"
        "com.reddit.frontpage" -> "👾"
        "com.facebook.katana" -> "📘"
        "com.pinterest" -> "📌"
        "com.instagram.barcelona" -> "🧵"
        "com.whatsapp" -> "💬"
        "org.telegram.messenger" -> "✈️"
        "com.discord" -> "🎮"
        "com.linkedin.android" -> "💼"
        else -> "📱"
    }
}
