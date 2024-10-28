package dev.achmad.core.network.interceptor

import dev.achmad.core.preference.getAndSet
import dev.achmad.core.network.NetworkPreferences
import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor(
    private val networkPreferences: NetworkPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val cookies = networkPreferences.cookies().get()
        var request = chain.request()

        if (cookies.isNotEmpty()) {
            request = request.newBuilder()
                .addHeader("Cookie", cookies.joinToString("; "))
                .build()
        }

        val response = chain.proceed(request)
        val newCookies = response.headers("Set-Cookie").toSet()
        if (newCookies.isNotEmpty()) {
            networkPreferences.cookies().set(newCookies)
        }

        response.priorResponse?.let { priorResponse ->
            val pathSegments = priorResponse.request.url.pathSegments
            if (priorResponse.code == 307 && pathSegments.any { it == "oauth" }) {
                // get cookie from discord or github oauth
                if (pathSegments.any { it == "discord" || it == "github" }) {
                    val cookieFromDiscord = priorResponse.headers("set-cookie").toSet()
                    networkPreferences.cookies().set(cookieFromDiscord)
                }
            }
        }

        return response
    }
}
