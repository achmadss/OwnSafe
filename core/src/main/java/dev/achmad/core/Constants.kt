package dev.achmad.core

// Shared Preferences
const val SHARED_PREFERENCES_NAME = "ownsafe_pref"
const val APP_PREF = "app_pref_"
const val NETWORK_PREF = "network_pref_"
enum class AppTheme {
    SYSTEM, DARK, LIGHT
}

// Network
const val DEFAULT_HOST = "moe-api.achmad.dev"

// OAuth
enum class OAuthType {
    DISCORD,
    GITHUB
}

val mimeTypes = listOf(
    "image", "video"
)