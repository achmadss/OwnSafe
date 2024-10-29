package dev.achmad.ownsafe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.achmad.core.network.APICallResult
import dev.achmad.core.network.NetworkPreferences
import dev.achmad.core.util.logcat
import dev.achmad.data.api.user.UpdateUserRequestBody
import dev.achmad.data.api.user.UserDataSource
import dev.achmad.data.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainState(
    val user: User? = null,
    val userLoading: Boolean = false,
)

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
    applicationPreferences: ApplicationPreferences,
    networkPreferences: NetworkPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    val appTheme = applicationPreferences
        .appTheme()
        .stateIn(viewModelScope)

    val dynamicColors = applicationPreferences
        .dynamicColors()
        .stateIn(viewModelScope)

    val cookies = networkPreferences
        .cookies()
        .stateIn(viewModelScope)

    fun getUser(
        onError: (errorMessage: String?) -> Unit = {},
    ) = viewModelScope.launch {
        when (val result = userDataSource.getUser()) {
            is APICallResult.Error -> {
                _state.update { it.copy(userLoading = false) }
                onError(result.error.message)
                return@launch
            }

            is APICallResult.Success -> {
                _state.update {
                    it.copy(user = result.data, userLoading = false)
                }
            }
        }
    }

    fun saveProfile(
        username: String,
        password: String? = null,
        onComplete: (errorMessage: String?) -> Unit = {},
    ) {
        val body = UpdateUserRequestBody(username = username, password = password)
        _state.update { it.copy(userLoading = true) }
        viewModelScope.launch {
            when (val result = userDataSource.updateUser(body)) {
                is APICallResult.Error -> {
                    _state.update { it.copy(userLoading = false) }
                    onComplete(result.error.message)
                    logcat { "code: ${result.code} | error: ${result.error.message}" }
                }

                is APICallResult.Success -> {
                    _state.update {
                        it.copy(user = result.data, userLoading = false)
                    }
                    onComplete(null)
                }
            }
        }
    }

}