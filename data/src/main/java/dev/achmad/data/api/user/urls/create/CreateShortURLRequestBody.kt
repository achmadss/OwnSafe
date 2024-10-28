package dev.achmad.data.api.user.urls.create

import com.google.gson.annotations.SerializedName

data class CreateShortURLRequestBody(
    @SerializedName("url")
    val url: String,
    @SerializedName("vanity")
    val vanity: String? = null
)
