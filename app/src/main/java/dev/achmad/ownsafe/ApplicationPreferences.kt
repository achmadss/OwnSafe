package dev.achmad.ownsafe

import dev.achmad.core.APP_PREF
import dev.achmad.core.AppTheme
import dev.achmad.core.preference.PreferenceStore
import dev.achmad.core.preference.getEnum

class ApplicationPreferences(
    private val preferenceStore: PreferenceStore,
) {
    private val prefix: String = APP_PREF

    fun appTheme() = preferenceStore.getEnum(prefix.plus("app_theme"), AppTheme.SYSTEM)
    fun dynamicColors() = preferenceStore.getBoolean(prefix.plus("dynamic_colors"), true)
    fun alwaysShowNavLabels() = preferenceStore.getBoolean(prefix.plus("always_show_nav_labels"), true)

}