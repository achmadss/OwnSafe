package dev.achmad.ownsafe.ui.settings.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.achmad.core.network.APICallResult
import dev.achmad.data.api.stats.StatsDataSource
import dev.achmad.data.entity.Stats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsState(
    val stats: Stats = Stats(),
    val statsLoading: Boolean = true,
    val statsError: Boolean = false,
    val statsFirstLoad: Boolean = true,
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statsDataSource: StatsDataSource,
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
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

}