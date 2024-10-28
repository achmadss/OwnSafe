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
    fun dynamicColor() = preferenceStore.getBoolean(prefix.plus("dynamic_color"), true)

}