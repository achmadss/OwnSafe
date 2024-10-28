package dev.achmad.data.api.auth.register

import com.google.gson.annotations.SerializedName

data class RegisterRequestBody(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
)
