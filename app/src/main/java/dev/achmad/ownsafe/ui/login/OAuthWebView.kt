package dev.achmad.ownsafe.ui.login

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OAuthWebView(
    modifier: Modifier = Modifier,
    url: String,
    host: String,
    onProgressChanged: (Int) -> Unit = {},
    onPageFinished: (WebView?, String?) -> Unit = { _, _ -> },
    onCodeReceived: (String) -> Unit,
    onDeny: (String) -> Unit,
) {
    // add top bar & loading bar underneath
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.databaseEnabled = true
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        onProgressChanged(newProgress)
                        super.onProgressChanged(view, newProgress)
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        onPageFinished(view, url)
                        super.onPageFinished(view, url)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        request?.url?.let {
                            if (it.host == host) {
                                it.getQueryParameter("code")?.let {
                                    if (it.isNotEmpty()) onCodeReceived(it)
                                    return true
                                }
                                val error = it.getQueryParameter("error")
                                val errorDescription = it.getQueryParameter("error_description")
                                if (error != null && errorDescription != null) {
                                    onDeny("Error: ${error.split("_").joinToString(" ")}\n${errorDescription.split("+").joinToString(" ")}")
                                }
                                return true
                            }
                        }
                        return false
                    }
                }
                loadUrl(url)
            }
        }
    )
}