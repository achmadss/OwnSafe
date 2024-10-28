package dev.achmad.data.entity

import com.google.gson.annotations.SerializedName
import dev.achmad.core.util.ZonedDateTimeString
import dev.achmad.core.util.toZonedDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class ShortURL(
    @SerializedName("createdAt")
    private val createdZonedDateTimeString: ZonedDateTimeString,
    @SerializedName("id")
    val id: String,
    @SerializedName("destination")
    val destination: String,
    @SerializedName("vanity")
    val vanity: String?,
    @SerializedName("views")
    val views: Int,
    @SerializedName("url")
    val url: String
) {
    val createdAt: ZonedDateTime
        get() = createdZonedDateTimeString.toZonedDateTime()
}
