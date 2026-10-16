package me.rerere.rikkahub.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Set ikon Sea Glass: garis membulat (round cap/join) setebal 1.8 + lapisan isi duotone
 * transparan, supaya terasa seperti kaca laut dan bukan ikon Android yang monoton.
 * Warna ikon diatur lewat tint, jadi ikut warna kaca (versi gelap dari tint-nya).
 */
object SeaIcons {
    private fun icon(name: String, stroke: String, fill: String? = null): ImageVector {
        val b = ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        )
        // Gaya Kimi: garis tipis tanpa isi; hanya ikon "solid" (Stop) yang terisi penuh.
        if (fill != null && name == "SeaStop") {
            b.addPath(
                pathData = addPathNodes(fill),
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
            )
        }
        b.addPath(
            pathData = addPathNodes(stroke),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.6f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
        return b.build()
    }

    private const val CUBE = "M12 3l8 4.5v9L12 21l-8-4.5v-9z"
    private const val HEART = "M12 20s-8-4.7-8-10.5A4.5 4.5 0 0 1 12 7a4.5 4.5 0 0 1 8 2.5C20 15.3 12 20 12 20z"
    private const val SPARK = "M12 3c.6 4.6 3.4 7.4 9 9-5.6 1.6-8.4 4.4-9 9-.6-4.6-3.4-7.4-9-9 5.6-1.6 8.4-4.4 9-9z"
    private const val BOOK = "M6 4h10a2 2 0 0 1 2 2v14H8a2 2 0 0 1-2-2z"
    private const val FRAME = "M6 4h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2z"

    /** Pengganti Settings03 */
    val Sliders by lazy {
        icon(
            "SeaSliders",
            "M4 7h8M18 7h2M4 17h2M12 17h8M12 7a2 2 0 1 0 4 0a2 2 0 1 0-4 0zM6 17a2 2 0 1 0 4 0a2 2 0 1 0-4 0z",
            "M12 7a2 2 0 1 0 4 0a2 2 0 1 0-4 0zM6 17a2 2 0 1 0 4 0a2 2 0 1 0-4 0z",
        )
    }
    val Display by lazy { icon("SeaDisplay", "$FRAME M4 9h16", "M6 4h12a2 2 0 0 1 2 2v3H4V6a2 2 0 0 1 2-2z") }
    val Cube by lazy { icon("SeaCube", "$CUBE M4 7.5l8 4.5 8-4.5M12 12v9", CUBE) }
    val Spark by lazy { icon("SeaSpark", SPARK, SPARK) }
    val Brain by lazy {
        icon(
            "SeaBrain",
            "M9 5a3 3 0 0 0-3 3 3 3 0 0 0-2 3 3 3 0 0 0 2 3 3 3 0 0 0 3 4 2.5 2.5 0 0 0 3-2V7a2 2 0 0 0-3-2zM15 5a3 3 0 0 1 3 3 3 3 0 0 1 2 3 3 3 0 0 1-2 3 3 3 0 0 1-3 4 2.5 2.5 0 0 1-3-2",
            "M9 5a3 3 0 0 0-3 3 3 3 0 0 0-2 3 3 3 0 0 0 2 3 3 3 0 0 0 3 4 2.5 2.5 0 0 0 3-2V7a2 2 0 0 0-3-2z",
        )
    }
    val Globe by lazy {
        icon(
            "SeaGlobe",
            "M12 3a9 9 0 1 0 0 18a9 9 0 1 0 0-18zM3 12h18M12 3c2.5 2.6 3.7 5.6 3.7 9s-1.2 6.4-3.7 9c-2.5-2.6-3.7-5.6-3.7-9S9.500 5.600 12 3z",
            "M12 3a9 9 0 1 0 0 18a9 9 0 1 0 0-18z",
        )
    }
    val Megaphone by lazy {
        icon(
            "SeaMegaphone",
            "M4 10v4a1 1 0 0 0 1 1h3l8 4V5L8 9H5a1 1 0 0 0-1 1zM19 9a4 4 0 0 1 0 6",
            "M8 9l8-4v14l-8-4z",
        )
    }
    val Picture by lazy {
        icon(
            "SeaPicture",
            "$FRAME M8.500 10a1.500 1.500 0 1 0 3 0a1.500 1.500 0 1 0-3 0zM4 16l4.500-4 4 3.500 3-2.500L20 16",
            FRAME,
        )
    }
    val Link by lazy {
        icon(
            "SeaLink",
            "M10 14l4-4M8.500 11.500l-2 2a3.200 3.200 0 0 0 4.500 4.500l2-2M15.500 12.500l2-2A3.200 3.200 0 0 0 13 6l-2 2",
        )
    }
    val Server by lazy {
        icon(
            "SeaServer",
            "M5 4h14a1.500 1.500 0 0 1 1.500 1.500v4A1.500 1.500 0 0 1 19 11H5A1.500 1.500 0 0 1 3.500 9.500v-4A1.500 1.500 0 0 1 5 4zM5 13h14a1.500 1.500 0 0 1 1.500 1.500v4A1.500 1.500 0 0 1 19 20H5a1.500 1.500 0 0 1-1.500-1.500v-4A1.500 1.500 0 0 1 5 13zM7.500 7.500h.01M7.500 16.500h.01",
            "M5 4h14a1.500 1.500 0 0 1 1.500 1.500v4A1.500 1.500 0 0 1 19 11H5A1.500 1.500 0 0 1 3.500 9.500v-4A1.500 1.500 0 0 1 5 4z",
        )
    }
    val Database by lazy {
        icon(
            "SeaDatabase",
            "M4 6c0-1.700 3.600-3 8-3s8 1.300 8 3-3.600 3-8 3-8-1.300-8-3zM4 6v6c0 1.700 3.600 3 8 3s8-1.300 8-3V6M4 12v6c0 1.700 3.600 3 8 3s8-1.300 8-3v-6",
            "M4 6c0-1.700 3.600-3 8-3s8 1.300 8 3-3.600 3-8 3-8-1.300-8-3z",
        )
    }
    val PictureUp by lazy {
        icon("SeaPictureUp", "$FRAME M12 16v-5M9.500 13l2.500-2.500 2.500 2.500", FRAME)
    }
    val Party by lazy {
        icon(
            "SeaParty",
            "M12 3l2.600 5.400 5.900.8-4.300 4.100 1 5.800L12 16.300 6.800 19.100l1-5.800L3.500 9.200l5.900-.8z",
            "M12 3l2.600 5.400 5.900.8-4.300 4.100 1 5.800L12 16.300 6.800 19.100l1-5.800L3.500 9.200l5.900-.8z",
        )
    }
    val Book by lazy { icon("SeaBook", "$BOOK M6 18a2 2 0 0 1 2-2h10", BOOK) }
    val Shelf by lazy {
        icon(
            "SeaShelf",
            "M5 4h3.500v16H5zM10.500 4H14v16h-3.500zM16 6.500l3.300-.9 3 14.200-3.300.9z",
            "M5 4h3.500v16H5zM10.500 4H14v16h-3.500z",
        )
    }
    val Heart by lazy { icon("SeaHeart", HEART, HEART) }
    val Share by lazy {
        icon(
            "SeaShare",
            "M8 12l8-5M8 12l8 5M4.500 12a1.800 1.800 0 1 0 3.600 0a1.800 1.800 0 1 0-3.600 0zM15.900 6.500a1.800 1.800 0 1 0 3.600 0a1.800 1.800 0 1 0-3.600 0zM15.900 17.500a1.800 1.800 0 1 0 3.600 0a1.800 1.800 0 1 0-3.600 0z",
            "M4.500 12a1.800 1.800 0 1 0 3.600 0a1.800 1.800 0 1 0-3.600 0zM15.900 6.500a1.800 1.800 0 1 0 3.600 0a1.800 1.800 0 1 0-3.600 0zM15.900 17.500a1.800 1.800 0 1 0 3.600 0a1.800 1.800 0 1 0-3.600 0z",
        )
    }
    val Phone by lazy {
        icon(
            "SeaPhone",
            "M8 3h8a1.500 1.500 0 0 1 1.500 1.500v15A1.500 1.500 0 0 1 16 21H8a1.500 1.500 0 0 1-1.500-1.500v-15A1.500 1.500 0 0 1 8 3zM10.500 18h3",
            "M8 3h8a1.500 1.500 0 0 1 1.500 1.500v15A1.500 1.500 0 0 1 16 21H8a1.500 1.500 0 0 1-1.500-1.500v-15A1.500 1.500 0 0 1 8 3z",
        )
    }
    val Sun by lazy {
        icon(
            "SeaSun",
            "M8 12a4 4 0 1 0 8 0a4 4 0 1 0-8 0zM12 3v2M12 19v2M3 12h2M19 12h2M5.600 5.600L7 7M17 17l1.400 1.400M5.600 18.400L7 17M17 7l1.400-1.400",
            "M8 12a4 4 0 1 0 8 0a4 4 0 1 0-8 0z",
        )
    }
    val Moon by lazy {
        icon(
            "SeaMoon",
            "M20 14.500A8 8 0 1 1 9.500 4a6.500 6.500 0 0 0 10.500 10.500z",
            "M20 14.500A8 8 0 1 1 9.500 4a6.500 6.500 0 0 0 10.500 10.500z",
        )
    }

    val Video by lazy {
        icon(
            "SeaVideo",
            "M5 6h9a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2zM16 10.500l4.500-2.500v8l-4.500-2.500",
            "M5 6h9a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2z",
        )
    }

    /** Open Search: globe + kaca pembesar */
    val OpenSearch by lazy {
        icon(
            "SeaOpenSearch",
            "M11 3a8 8 0 1 0 0 16a8 8 0 1 0 0-16zM3 11h16M11 3c2.2 2.3 3.3 5 3.3 8S13.200 16.700 11 19c-2.200-2.300-3.300-5-3.300-8S8.800 5.300 11 3zM17.500 17.500L21 21",
            "M11 3a8 8 0 1 0 0 16a8 8 0 1 0 0-16z",
        )
    }

    val ChevronRight by lazy { icon("SeaChevronRight", "M9.500 6l6 6-6 6") }
    val ChatAdd by lazy {
        icon("SeaChatAdd", "M12 4a8 8 0 0 0-6.900 12L4 20l4.100-1.100A8 8 0 1 0 12 4zM12 9v6M9 12h6")
    }

    /** Tombol kirim: panah ke atas yang membulat */
    val Send by lazy { icon("SeaSend", "M12 19V6M6.500 11.500L12 6l5.500 5.500") }
    val Stop by lazy {
        icon(
            "SeaStop",
            "M9 8h6a1 1 0 0 1 1 1v6a1 1 0 0 1-1 1H9a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z",
            "M9 8h6a1 1 0 0 1 1 1v6a1 1 0 0 1-1 1H9a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1z",
        )
    }
}
