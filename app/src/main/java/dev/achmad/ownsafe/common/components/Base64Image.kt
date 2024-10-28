package dev.achmad.ownsafe.common.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun Base64Image(
    modifier: Modifier = Modifier,
    base64String: String,
) {
    AsyncImage(
        modifier = modifier,
        model = Base64.decode(base64String.substringAfter("base64,")),
        contentDescription = null,
    )
}
