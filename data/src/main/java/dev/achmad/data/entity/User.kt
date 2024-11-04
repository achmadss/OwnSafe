package dev.achmad.data.entity

import com.google.gson.annotations.SerializedName
import dev.achmad.core.OAuthType

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("oauth")
    val oauth: List<OAuth>,
) {
    data class OAuth(
        @SerializedName("oauth_provider")
        val provider: OAuthType
    )

    companion object {
        fun dummyUser() = User(
            id = 1,
            username = "dummy",
            avatar = "",
            oauth = emptyList(),
        )
    }
}
