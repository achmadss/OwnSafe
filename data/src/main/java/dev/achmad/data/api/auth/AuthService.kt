package dev.achmad.data.api.auth

import dev.achmad.data.api.auth.login.LoginRequestBody
import dev.achmad.data.api.auth.login.LoginResponseBody
import dev.achmad.data.api.auth.register.RegisterRequestBody
import dev.achmad.data.api.auth.register.RegisterResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

private const val prefix = "auth"

interface AuthService {

    @POST("$prefix/login")
    suspend fun login(
        @Body body: LoginRequestBody
    ): Response<LoginResponseBody>

    @POST("$prefix/register")
    suspend fun register(
        @Body body: RegisterRequestBody
    ): Response<RegisterResponseBody>

    @GET("$prefix/oauth/{type}")
    suspend fun oauth(
        @Path("type") type: String,
        @Query("code") code: String = "",
    ): Response<Any>

}