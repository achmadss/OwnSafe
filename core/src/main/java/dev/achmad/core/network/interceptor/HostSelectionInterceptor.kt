package dev.achmad.core.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HostSelectionInterceptor(
    private val host: () -> String
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val currentHost = host()

        val newUrl = request.url.newBuilder()
            .host(currentHost)
            .build()
        request = request.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(request)
    }
}
