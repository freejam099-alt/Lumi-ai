package me.rerere.search

import me.rerere.search.SearchResult.SearchResultItem

data class KeylessBackendInfo(
    val name: String,
    val role: String,
    val status: String? = null,
)

internal data class KeylessHttpResponse(
    val statusCode: Int,
    val body: String,
)

internal data class KeylessGeo(
    val name: String,
    val country: String = "",
    val latitude: Double,
    val longitude: Double,
) {
    val displayName: String
        get() = listOf(name, country).filter { it.isNotBlank() }.joinToString(", ")
}

internal data class KeylessFetch(
    val backend: String,
    val items: List<SearchResultItem> = emptyList(),
    val images: List<KeylessImage> = emptyList(),
    val answer: String? = null,
) {
    val isUseful: Boolean
        get() = items.isNotEmpty() || images.isNotEmpty() || !answer.isNullOrBlank()
}

internal fun searchResultImage(
    url: String,
    title: String = "",
    thumbnailUrl: String? = null,
    sourcePageUrl: String? = null,
): KeylessImage {
    val alt = title.ifBlank { "Image" }.replace("]", "\\]")
    return KeylessImage(
        url = url,
        title = title,
        thumbnailUrl = thumbnailUrl?.takeIf { it.isNotBlank() },
        sourcePageUrl = sourcePageUrl?.takeIf { it.isNotBlank() },
        markdownImage = "![$alt]($url)",
    )
}

internal fun SearchResultItem.withSource(source: String?, publishedDate: String? = this.publishedDate): SearchResultItem {
    return copy(
        source = source?.takeIf { it.isNotBlank() },
        publishedDate = publishedDate?.takeIf { it.isNotBlank() },
    )
}
