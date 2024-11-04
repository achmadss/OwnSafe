package dev.achmad.ownsafe.ui.home.shorturl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.achmad.data.entity.ShortURL
import dev.achmad.ownsafe.common.components.SwipeToDeleteContainer
import kotlinx.serialization.Serializable

@Serializable
object ShortURLRoute

fun NavGraphBuilder.shortUrlDestination() {
    composable<ShortURLRoute> {
        val viewModel = hiltViewModel<ShortURLViewModel>()
        val state by viewModel.state.collectAsState()

        ShortURLScreen(
            state = state,
            onDelete = {

            },
            onRefresh = {
                viewModel.getShortURLs()
            },
            onCreateNewShortURL = {

            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShortURLScreen(
    state: ShortURLState,
    onDelete: (ShortURL) -> Unit,
    onRefresh: () -> Unit,
    onCreateNewShortURL: () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    var fabExpanded by remember { mutableStateOf(true) }
    var firstTimeLoad by remember { mutableStateOf(true) }
    val isRefreshing = when (state) {
        is ShortURLState.Loading -> true
        else -> false
    }
    val shortURLs = when (state) {
        is ShortURLState.Success -> state.shortURLs
        else -> emptyList()
    }

    LaunchedEffect(lazyListState.lastScrolledForward) {
        if (!firstTimeLoad) fabExpanded = lazyListState.lastScrolledForward
    }

    PullToRefreshBox(
        modifier = Modifier
            .fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        if (!isRefreshing && shortURLs.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "You don't have any Short URL")
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            contentPadding = PaddingValues(bottom = 56.dp.plus(16.dp))
        ) {
            items(
                items = shortURLs,
                key = { it.hashCode() }
            ) {
                SwipeToDeleteContainer(
                    item = it,
                    onDelete = { onDelete(it) }
                ) {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background),
                        headlineContent = {
                            Text(text = it.vanity ?: it.id)
                        },
                        supportingContent = {
                            Text(
                                text = it.destination,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingContent = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "Views")
                                Text(text = it.views.toString())
                            }
                        }
                    )
                }
            }
        }
        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            expanded = fabExpanded,
            text = { Text(text = "Create") },
            icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
            onClick = { if (!isRefreshing) onCreateNewShortURL() }
        )
    }
}