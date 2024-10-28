package dev.achmad.ownsafe.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.achmad.core.network.NetworkPreferences
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val networkPreferences: NetworkPreferences,
) : ViewModel() {

    fun logout() = networkPreferences.cookies().delete()

}