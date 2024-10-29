package dev.achmad.ownsafe.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.achmad.ownsafe.ApplicationPreferences
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val applicationPreferences: ApplicationPreferences,
) : ViewModel() {

    val alwaysShowNavLabels = applicationPreferences
        .alwaysShowNavLabels()
        .stateIn(viewModelScope)

}