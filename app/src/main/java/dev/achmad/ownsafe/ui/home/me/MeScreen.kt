package dev.achmad.ownsafe.ui.home.me

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.achmad.ownsafe.LocalMainState
import dev.achmad.ownsafe.common.components.Base64Image
import dev.achmad.ownsafe.common.extension.noRippleClickable
import kotlinx.serialization.Serializable

@Serializable
object Me

fun NavGraphBuilder.meDestination(
    onNavigateToProfile: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToDataAndStorage: () -> Unit,
    onNavigateToAppearance: () -> Unit,
) {
    composable<Me> {
        val viewModel = hiltViewModel<MeViewModel>()
        val mainState by LocalMainState.current.collectAsState()

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
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsItem(
                        icon = Icons.Default.QueryStats,
                        title = "Statistics",
                        onClick = onNavigateToStatistics
                    )
                    SettingsItem(
                        icon = Icons.Default.Storage,
                        title = "Data and storage",
                        onClick = onNavigateToDataAndStorage
                    )
                    SettingsItem(
                        icon = Icons.Outlined.Palette,
                        title = "Appearance",
                        onClick = onNavigateToAppearance
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(text = title)
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
