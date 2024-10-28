package dev.achmad.ownsafe.ui.home.me

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import dev.achmad.core.AppTheme
import dev.achmad.ownsafe.LocalMainState
import dev.achmad.ownsafe.common.components.Base64Image
import dev.achmad.ownsafe.common.extension.noRippleClickable
import dev.achmad.ownsafe.common.extension.toast
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
object Me

fun NavGraphBuilder.meDestination(
    onNavigateToProfile: () -> Unit,
) {
    composable<Me> {
        val viewModel = hiltViewModel<MeViewModel>()
        val context = LocalContext.current
        val mainState by LocalMainState.current.collectAsState()
        val state by viewModel.state.collectAsState()
        val appTheme by viewModel.appTheme.collectAsState()

        LaunchedEffect(Unit) {
            if (state.statsFirstLoad) {
                viewModel.getLatestStats(
                    setFirstLoad = false,
                    onError = { context.toast(it) }
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            mainState.user?.let { user ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .align(Alignment.Center),
                ) {
                    ProfileSection(
                        base64String = user.avatar,
                        username = user.username,
                        onClickProfile = onNavigateToProfile
                    )
                    StatsSection(
                        loading = state.statsLoading,
                        media = when {
                            state.statsLoading -> "--"
                            state.statsError -> "--"
                            else -> {
                                state.stats.mediaCount().toString()
                            }
                        },
                        files = when {
                            state.statsLoading -> "--"
                            state.statsError -> "--"
                            else -> state.stats.filesCount().toString()
                        },
                        storage = when {
                            state.statsLoading -> "--"
                            state.statsError -> "--"
                            else -> state.stats.humanReadableSize
                        },
                        views = when {
                            state.statsLoading -> "--"
                            state.statsError -> "--"
                            else -> state.stats.totalViews.toString()
                        },
                        onRefresh = {
                            viewModel.getLatestStats(
                                onError = { context.toast(it) }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // TODO refactor
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Theme")
                        Spacer(modifier = Modifier.height(8.dp))
                        MultiChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppTheme.entries.toTypedArray().onEachIndexed { index, theme ->
                                SegmentedButton(
                                    checked = appTheme == theme,
                                    onCheckedChange = {
                                        viewModel.updateTheme(theme)
                                    },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = AppTheme.entries.size
                                    )
                                ) {
                                    Text(text = theme.name.lowercase().replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(
    base64String: String,
    username: String,
    onClickProfile: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onClickProfile() }
            .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Base64Image(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            base64String = base64String
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = username,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View profile",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun StatsSection(
    loading: Boolean = false,
    media: String = "--",
    files: String = "--",
    storage: String = "--",
    views: String = "--",
    onRefresh: () -> Unit,
) {
    val fontColor =
        if (loading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        else MaterialTheme.colorScheme.onSurface
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Stats",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                modifier = Modifier
                    .noRippleClickable { onRefresh() },
                text = "Refresh",
                color = if (!loading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.38f
                ),
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        StatsItem(
            icon = Icons.Default.PhotoLibrary,
            title = "Media",
            value = media,
            color = fontColor,
        )
        StatsItem(
            icon = Icons.AutoMirrored.Filled.InsertDriveFile,
            title = "Files",
            value = files,
            color = fontColor,
        )
        StatsItem(
            icon = Icons.Default.Storage,
            title = "Storage",
            value = storage,
            color = fontColor,
        )
        StatsItem(
            icon = Icons.Default.Visibility,
            title = "Views",
            value = views,
            color = fontColor,
        )
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
            .padding(vertical = 8.dp),
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