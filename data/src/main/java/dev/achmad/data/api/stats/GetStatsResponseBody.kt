package dev.achmad.data.api.stats

import com.google.gson.annotations.SerializedName
import dev.achmad.core.util.ZonedDateTimeString
import dev.achmad.core.util.toZonedDateTime
import dev.achmad.data.entity.Stats
import java.time.ZonedDateTime

data class GetStatsResponseBody(
    @SerializedName("id")
    val id: Int,
    @SerializedName("createdAt")
    private val createdZonedDateTimeString: ZonedDateTimeString,
    @SerializedName("data")
    val data: Stats = Stats()
) {
    val createdAt : ZonedDateTime
        get() = createdZonedDateTimeString.toZonedDateTime()
}
