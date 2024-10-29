package dev.achmad.ownsafe.ui.settings.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.achmad.data.entity.Stats
import dev.achmad.ownsafe.common.extension.toast
import kotlinx.serialization.Serializable

@Serializable
object Statistics

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.statisticsDestination(
    onBack: () -> Unit,
) {
    composable<Statistics> {
        val viewModel = hiltViewModel<StatisticsViewModel>()
        val state by viewModel.state.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            if (state.statsFirstLoad) {
                viewModel.getLatestStats(
                    setFirstLoad = false,
                    onError = { context.toast(it) }
                )
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Statistics")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                viewModel.getLatestStats(
                                    onError = { context.toast(it) }
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = if (!state.statsLoading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.38f
                                ),
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
            ) {
                StatsSection(
                    loading = state.statsLoading,
                    stats = state.stats
                )
            }
        }

    }
}


@Composable
private fun StatsSection(
    loading: Boolean = false,
    stats: Stats,
) {
    val fontColor =
        if (loading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        else MaterialTheme.colorScheme.onSurface
    val items = listOf(
        Triple(
            Icons.Outlined.PhotoLibrary,
            "Media",
            stats.mediaCount().toString(),
        ),
        Triple(
            Icons.AutoMirrored.Outlined.InsertDriveFile,
            "Files",
            stats.filesCount().toString(),
        ),
        Triple(
            Icons.Default.Storage,
            "Storage",
            stats.humanReadableSize,
        ),
        Triple(
            Icons.Default.Visibility,
            "Views",
            stats.totalViews.toString(),
        )
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items.forEach {
            StatsItem(
                icon = it.first,
                title = it.second,
                value = it.third,
                color = fontColor
            )
        }
    }
}

@Composable
private fun StatsItem(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = color)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Right,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = color
        )
    }
}