package dev.achmad.ownsafe.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    animationDuration: Int = 300,
    content: @Composable (T) -> Unit
) {
    var deleteItem by remember { mutableStateOf(false) }
    var currentState by remember { mutableStateOf<SwipeToDismissBoxValue?>(null) }
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd,
                SwipeToDismissBoxValue.EndToStart -> {
                    deleteItem = true
                    currentState = dismissValue
                }
                else -> Unit
            }
            false // Immediately resets the state so we can swipe it again if deletion fails
        },
        positionalThreshold = { it.div(3) }
    )

    LaunchedEffect(currentState) {
        currentState?.let {
            state.snapTo(it)
            currentState = null
        }
    }

    LaunchedEffect(deleteItem) {
        if (deleteItem) {
            delay(animationDuration.toLong())
            onDelete(item)
        } else {
            state.reset()
        }
    }

    AnimatedVisibility(
        visible = !deleteItem,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut(),
    ) {
        SwipeToDismissBox(
            state = state,
            backgroundContent = {
                DeleteBackground(
                    swipeDismissBoxState = state,
                )
            },
            content = { content(item) }
        )
    }
}

@Composable
fun DeleteBackground(
    swipeDismissBoxState: SwipeToDismissBoxState,
    backgroundColor: Color = MaterialTheme.colorScheme.errorContainer,
) {
    val color = when (swipeDismissBoxState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart -> backgroundColor
        else -> Color.Transparent
    }
    val alignment = when (swipeDismissBoxState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.CenterEnd
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = alignment
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = Color.White
        )
    }
}