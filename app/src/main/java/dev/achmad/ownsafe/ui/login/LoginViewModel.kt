package dev.achmad.ownsafe.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.achmad.core.OAuthType
import dev.achmad.core.util.logcat
import dev.achmad.core.network.APICallResult
import dev.achmad.core.network.NetworkPreferences
import dev.achmad.data.api.auth.AuthDataSource
import dev.achmad.data.api.auth.login.LoginRequestBody
import dev.achmad.data.api.auth.register.RegisterRequestBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OAuthModel(
    val url: String? = null,
    val type: OAuthType
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val networkPreferences: NetworkPreferences,
) : ViewModel() {

    val host = networkPreferences.host()

    private val _showWebView = MutableStateFlow(false)
    val showWebView = _showWebView.asStateFlow()

    private val _oauthModel = MutableStateFlow<OAuthModel?>(null)
    val oauthModel = _oauthModel.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun showWebView(visible: Boolean) {
        _showWebView.update { visible }
        if (!visible) resetOAuth()
    }

    fun login(
        username: String,
        password: String,
        onComplete: (error: String?) -> Unit,
    ) {
        val body = LoginRequestBody(username, password)
        viewModelScope.launch {
            _loading.update { true }
            when (val result = authDataSource.login(body)) {
                is APICallResult.Error -> {
                    onComplete(result.error.message)
                    logcat { "code: ${result.code} | error: ${result.error.message}" }
                }

                is APICallResult.Success -> onComplete(null)
            }
            _loading.update { false }
        }
    }

    fun register(
        username: String,
        password: String,
        onComplete: (String?) -> Unit,
    ) {
        val body = RegisterRequestBody(username, password)
        viewModelScope.launch {
            _loading.update { true }
            val registerResult = authDataSource.register(body)
            if (registerResult is APICallResult.Error) {
                onComplete(registerResult.error.message)
                logcat { "code: ${registerResult.code} | error: ${registerResult.error.message}" }
            }
            val loginResult = authDataSource.login(LoginRequestBody(username, password))
            if (loginResult is APICallResult.Error) {
                onComplete(loginResult.error.message)
                logcat { "code: ${loginResult.code} | error: ${loginResult.error.message}" }
            }
            onComplete(null)
            _loading.update { false }
        }
    }

    private fun resetOAuth() = _oauthModel.update { null }

    fun updateOAuth(type: OAuthType) {
        val url = "https://${host.get()}/api/auth/oauth/${type.name.lowercase()}"
        _oauthModel.update {
            OAuthModel(url = url, type = type)
        }
    }

    fun confirmLoginOauth(
        code: String,
        onError: (String?) -> Unit,
        onSuccess: () -> Unit,
    ) = viewModelScope.launch {
        _oauthModel.value?.let {
            _loading.update { true }
            when (val result = authDataSource.oauth(it.type.name.lowercase(), code)) {
                is APICallResult.Error -> {
                    result.error.printStackTrace()
                    if (result.code == 404) {
                        if (networkPreferences.cookies().isSet()) onSuccess()
                        else onError("Cannot get cookies")
                    } else onError(result.error.message)
                }

                else -> Unit
            }
            _loading.update { false }
            _oauthModel.update { null }
        }
    }

}