package dev.achmad.data.api.auth.login

import com.google.gson.annotations.SerializedName

data class LoginResponseBody(
    @SerializedName("success")
    val success: Boolean,
)
