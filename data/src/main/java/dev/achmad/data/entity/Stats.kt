package dev.achmad.data.entity

import com.google.gson.annotations.SerializedName
import dev.achmad.core.mimeTypes

data class Stats(
    @SerializedName("count")
    val totalFilesAndMedia: Int = 0,
    @SerializedName("size")
    val humanReadableSize: String = "--",
    @SerializedName("views_count")
    val totalViews: Int = 0,
    @SerializedName("types_count")
    val typesCount: List<TypeCount> = emptyList(),
) {
    data class TypeCount(
        @SerializedName("count")
        val count: Int,
        @SerializedName("mimetype")
        val mimeType: String = "",
    )

    fun mediaCount() = typesCount
        .filter { data ->
            mimeTypes.any { type ->
                data.mimeType.contains(type)
            }
        }
        .sumOf { it.count }
    
    fun filesCount() = typesCount
        .filter { data ->
            mimeTypes.none { type ->
                data.mimeType.contains(type)
            }
        }
        .sumOf { it.count }
}
