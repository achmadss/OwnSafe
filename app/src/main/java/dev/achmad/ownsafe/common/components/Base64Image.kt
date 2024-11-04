package dev.achmad.ownsafe.common.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImagePainter.State
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageScope
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun Base64Image(
    modifier: Modifier = Modifier,
    base64String: String?,
    loading: @Composable (SubcomposeAsyncImageScope.(State.Loading) -> Unit)? = null,
    success: @Composable (SubcomposeAsyncImageScope.(State.Success) -> Unit)? = {
        Image(painter = it.painter, contentDescription = null)
    },
    error: @Composable (SubcomposeAsyncImageScope.(State.Error) -> Unit)? = null,
) {
    SubcomposeAsyncImage(
        modifier = modifier,
        model = if (!base64String.isNullOrEmpty())
            Base64.decode(base64String.substringAfter("base64,")) else null,
        contentDescription = null,
        loading = loading,
        error = error,
        success = success
    )
}
