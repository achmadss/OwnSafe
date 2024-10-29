package dev.achmad.ownsafe.ui.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.achmad.core.AppTheme
import dev.achmad.ownsafe.ApplicationPreferences
import javax.inject.Inject

@HiltViewModel
class AppearanceViewModel @Inject constructor(
    private val applicationPreferences: ApplicationPreferences,
) : ViewModel() {

    val appTheme = applicationPreferences
        .appTheme()
        .stateIn(viewModelScope)

    val dynamicColors = applicationPreferences
        .dynamicColors()
        .stateIn(viewModelScope)

    val alwaysShowNavLabels = applicationPreferences
        .alwaysShowNavLabels()
        .stateIn(viewModelScope)

    fun updateTheme(theme: AppTheme) = applicationPreferences.appTheme().set(theme)
    fun updateDynamicColors(enabled: Boolean) = applicationPreferences.dynamicColors().set(enabled)
    fun updateAlwaysShowLabels(enabled: Boolean) = applicationPreferences.alwaysShowNavLabels().set(enabled)

}