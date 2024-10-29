package dev.achmad.ownsafe.ui.settings.appearance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.achmad.core.AppTheme
import dev.achmad.ownsafe.common.components.SettingsSection
import dev.achmad.ownsafe.common.components.SettingsToggleItem
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
object Appearance

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.appearanceDestination(
    onBack: () -> Unit,
) {
    composable<Appearance> {
        val viewModel = hiltViewModel<AppearanceViewModel>()
        val appTheme by viewModel.appTheme.collectAsState()
        val dynamicColors by viewModel.dynamicColors.collectAsState()
        val alwaysShowNavLabels by viewModel.alwaysShowNavLabels.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Appearance") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsSection(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Theme",
                ) {
                    MultiChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
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
                                Text(
                                    text = theme.name.lowercase().replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                                    }
                                )
                            }
                        }
                    }
                    SettingsToggleItem(
                        title = "Enable dynamic colors",
                        checked = dynamicColors,
                        onToggle = {
                            viewModel.updateDynamicColors(it)
                        }
                    )
                }
                SettingsSection(
                    modifier = Modifier.fillMaxWidth(),
                    title = "Navbar"
                ) {
                    SettingsToggleItem(
                        title = "Always show labels",
                        checked = alwaysShowNavLabels,
                        onToggle = {
                            viewModel.updateAlwaysShowLabels(it)
                        }
                    )
                }
            }
        }
    }
}
