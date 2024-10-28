package dev.achmad.data.api.user.urls.create

import com.google.gson.annotations.SerializedName

data class CreateShortURLResponseBody(
    @SerializedName("url")
    val url: String,
)
