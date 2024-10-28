package dev.achmad.data.api.auth.login

import com.google.gson.annotations.SerializedName

data class LoginRequestBody(

    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("code")
    val totp: String? = null,

)