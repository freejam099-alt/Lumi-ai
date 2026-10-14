package me.rerere.search

import android.util.Log
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.Locale

/** Gambar hasil pencarian (diteruskan ke SearchResult.images sebagai URL). */
internal data class KeylessImage(
    val url: String,
    val title: String = "",
    val thumbnailUrl: String? = null,
    val sourcePageUrl: String? = null,
    val markdownImage: String = "",
)

internal fun keylessLogW(tag: String, message: String) {
    Log.w(tag, message)
}

internal val SearchService.Companion.acceptLanguage: String
    get() = Locale.getDefault().let { "${it.language}-${it.country},${it.language}" }

internal val JsonElement?.jsonObjectOrNull: JsonObject? get() = this as? JsonObject
internal val JsonElement?.jsonArrayOrNull: JsonArray? get() = this as? JsonArray
internal val JsonElement?.jsonPrimitiveOrNull: JsonPrimitive? get() = this as? JsonPrimitive

internal fun String.urlEncode(spaceAsPlus: Boolean = false): String {
    val encoded = URLEncoder.encode(this, "UTF-8")
    return if (spaceAsPlus) encoded else encoded.replace("+", "%20")
}

internal fun String.urlDecode(plusAsSpace: Boolean = false): String {
    val input = if (plusAsSpace) this else this.replace("+", "%2B")
    return runCatching { URLDecoder.decode(input, "UTF-8") }.getOrDefault(this)
}

internal fun String.unescapeHtml(): String {
    val out = StringBuilder(length)
    var i = 0
    while (i < length) {
        val c = this[i]
        if (c != '&') { out.append(c); i++; continue }
        val semi = indexOf(';', i + 1)
        if (semi == -1 || semi - i > 10) { out.append(c); i++; continue }
        val entity = substring(i + 1, semi)
        val decoded: String? = when {
            entity == "amp" -> "&"
            entity == "lt" -> "<"
            entity == "gt" -> ">"
            entity == "quot" -> "\""
            entity == "apos" -> "'"
            entity == "nbsp" -> "\u00A0"
            entity.startsWith("#x", ignoreCase = true) ->
                entity.substring(2).toIntOrNull(16)?.let { String(Character.toChars(it)) }
            entity.startsWith("#") ->
                entity.substring(1).toIntOrNull()?.let { String(Character.toChars(it)) }
            else -> null
        }
        if (decoded == null) { out.append(c); i++ } else { out.append(decoded); i = semi + 1 }
    }
    return out.toString()
}
