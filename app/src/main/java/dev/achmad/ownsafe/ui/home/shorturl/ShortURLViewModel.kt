package dev.achmad.ownsafe.ui.home.shorturl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.achmad.core.network.APICallResult
import dev.achmad.data.api.user.UserDataSource
import dev.achmad.data.entity.ShortURL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ShortURLState {
    data class Success(
        val shortURLs: List<ShortURL>,
    ) : ShortURLState

    data object Loading : ShortURLState
    data object Error : ShortURLState
}

@HiltViewModel
class ShortURLViewModel @Inject constructor(
    private val userDataSource: UserDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow<ShortURLState>(ShortURLState.Loading)
    val state = _state.asStateFlow()

    init {
        getShortURLs()
    }

    fun getShortURLs() = viewModelScope.launch {
        _state.update { ShortURLState.Loading }
        when (val result = userDataSource.getShortURLs()) {
            is APICallResult.Error -> {
                result.error.printStackTrace()
                _state.update { ShortURLState.Error }
            }

            is APICallResult.Success -> {
                _state.update { ShortURLState.Success(result.data) }
            }
        }
    }

}