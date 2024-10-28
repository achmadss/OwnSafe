package dev.achmad.data.api.user.urls.delete

import com.google.gson.annotations.SerializedName

data class DeleteShortURLRequestBody(
    @SerializedName("id")
    val id: String,
)
