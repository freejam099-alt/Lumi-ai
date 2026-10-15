package me.rerere.search

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull
import me.rerere.search.SearchResult.SearchResultItem

private fun String.stripTags(): String =
    replace(Regex("<[^>]+>"), " ").unescapeHtml().replace(Regex("\\s+"), " ").trim()

/** Mojeek: mesin pencari dengan indeks sendiri, HTML sederhana dan ramah scraping. */
internal fun parseMojeekHtml(html: String): List<SearchResultItem> {
    val items = LinkedHashMap<String, SearchResultItem>()
    val anchor = Regex("""<a\b([^>]*)>(.*?)</a>""", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
    val snippet = Regex("""<p\s+class="s"[^>]*>(.*?)</p>""", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
    val blocks = Regex("""<li\b[^>]*>(.*?)</li>""", setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.IGNORE_CASE))
        .findAll(html).map { it.groupValues[1] }.toList()
    for (block in blocks) {
        val a = anchor.findAll(block).firstOrNull { it.groupValues[1].contains("title") } ?: continue
        val href = Regex("""href="([^"]+)"""").find(a.groupValues[1])?.groupValues?.get(1)?.unescapeHtml() ?: continue
        if (!href.startsWith("http")) continue
        val title = a.groupValues[2].stripTags()
        if (title.isBlank()) continue
        val text = snippet.find(block)?.groupValues?.get(1)?.stripTags().orEmpty()
        items.putIfAbsent(href, SearchResultItem(title = title, url = href, text = text, source = "Mojeek"))
    }
    return items.values.toList()
}

/** Bing Images (endpoint async): tiap hasil punya atribut m="{...murl...}". */
internal fun parseBingImages(html: String): List<KeylessImage> {
    val out = LinkedHashMap<String, KeylessImage>()
    val attr = Regex("""\bm="(\{.*?\})"""", RegexOption.DOT_MATCHES_ALL)
    for (m in attr.findAll(html)) {
        val raw = m.groupValues[1].unescapeHtml()
        val murl = Regex(""""murl"\s*:\s*"([^"]+)"""").find(raw)?.groupValues?.get(1)?.replace("\\/", "/") ?: continue
        if (!murl.startsWith("http")) continue
        val title = Regex(""""t"\s*:\s*"([^"]*)"""").find(raw)?.groupValues?.get(1).orEmpty().unescapeHtml()
        val turl = Regex(""""turl"\s*:\s*"([^"]+)"""").find(raw)?.groupValues?.get(1)?.replace("\\/", "/")
        val page = Regex(""""purl"\s*:\s*"([^"]+)"""").find(raw)?.groupValues?.get(1)?.replace("\\/", "/")
        out.putIfAbsent(murl, searchResultImage(url = murl, title = title, thumbnailUrl = turl, sourcePageUrl = page))
    }
    if (out.isEmpty()) {
        Regex("""murl&quot;:&quot;(https?:[^&]+)&quot;""").findAll(html).forEach {
            val u = it.groupValues[1].replace("\\/", "/")
            out.putIfAbsent(u, searchResultImage(url = u))
        }
    }
    return out.values.toList()
}

/** Openverse: pustaka gambar berlisensi terbuka, JSON tanpa kunci. */
internal fun parseOpenverseImages(json: String): List<KeylessImage> {
    val root = runCatching { SearchService.json.parseToJsonElement(json).jsonObject }.getOrNull() ?: return emptyList()
    val results = root["results"]?.jsonArray ?: return emptyList()
    return results.mapNotNull { el ->
        val o = el as? JsonObject ?: return@mapNotNull null
        val url = o["url"]?.jsonPrimitive?.contentOrNull?.takeIf { it.startsWith("http") } ?: return@mapNotNull null
        searchResultImage(
            url = url,
            title = o["title"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            thumbnailUrl = o["thumbnail"]?.jsonPrimitive?.contentOrNull,
            sourcePageUrl = o["foreign_landing_url"]?.jsonPrimitive?.contentOrNull,
        )
    }
}
