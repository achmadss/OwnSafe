package dev.achmad.ownsafe.ui.home.me

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.achmad.core.AppTheme
import dev.achmad.core.network.APICallResult
import dev.achmad.data.api.stats.StatsDataSource
import dev.achmad.data.entity.Stats
import dev.achmad.ownsafe.ApplicationPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MeState(
    val stats: Stats = Stats(),
    val statsLoading: Boolean = true,
    val statsError: Boolean = false,
    val statsFirstLoad: Boolean = true,
)

@HiltViewModel
class MeViewModel @Inject constructor(
    private val statsDataSource: StatsDataSource,
    private val applicationPreferences: ApplicationPreferences,
) : ViewModel() {

    val appTheme = applicationPreferences
        .appTheme()
        .stateIn(viewModelScope)

    private val _state = MutableStateFlow(MeState())
    val state = _state.asStateFlow()

    fun getLatestStats(
        setFirstLoad: Boolean = false,
        onError: (errorMessage: String?) -> Unit = {},
    ) = viewModelScope.launch {
        _state.update {
            it.copy(
                statsLoading = true,
                statsError = false,
                statsFirstLoad = setFirstLoad
            )
        }
        when (val result = statsDataSource.getLatestStats()) {
            is APICallResult.Error -> {
                result.error.printStackTrace()
                _state.update { it.copy(statsLoading = false, statsError = true) }
                onError(result.error.message)
            }

            is APICallResult.Success -> {
                val data = result.data.firstOrNull()?.data
                data?.let { notNullData ->
                    _state.update {
                        it.copy(
                            stats = notNullData,
                            statsLoading = false,
                            statsError = false
                        )
                    }
                }
            }
        }
    }

    fun updateTheme(value: AppTheme) = applicationPreferences.appTheme().set(value)

}