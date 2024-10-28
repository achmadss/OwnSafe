package dev.achmad.data.api.auth.register

import com.google.gson.annotations.SerializedName

data class RegisterResponseBody(
    @SerializedName("message")
    val message: String
)
