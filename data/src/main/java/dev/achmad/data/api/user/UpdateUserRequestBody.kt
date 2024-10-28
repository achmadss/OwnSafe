package dev.achmad.data.api.user

data class UpdateUserRequestBody(
    val username: String,
    val password: String? = null,
    val domains: List<String> = emptyList(),
    val embed: Embed = Embed()
) {
    data class Embed(
        val title: String? = null,
        val color: String? = null,
        val siteName: String? = null,
        val description: String? = null
    )
}


