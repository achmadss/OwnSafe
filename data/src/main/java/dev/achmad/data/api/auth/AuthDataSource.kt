package dev.achmad.data.api.auth

import dev.achmad.core.network.await
import dev.achmad.data.api.auth.login.LoginRequestBody
import dev.achmad.data.api.auth.register.RegisterRequestBody

class AuthDataSource(
    private val authService: AuthService
) {
    suspend fun login(body: LoginRequestBody) = await { authService.login(body) }
    suspend fun register(body: RegisterRequestBody) = await { authService.register(body) }
    suspend fun oauth(type: String, code: String = "") = await { authService.oauth(type, code) }
}