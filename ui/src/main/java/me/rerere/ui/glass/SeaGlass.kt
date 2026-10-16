package me.rerere.ui.glass

import android.graphics.Bitmap
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Random

/**
 * Sea Glass (Material 4 concept), mengikuti referensi: tombol kaca berwarna dengan
 * inti lebih pekat yang lembut, tepi lebih terang, grain seperti terrazzo, dan bayangan
 * biru-abu yang jatuh ke bawah. Tidak ada border sama sekali.
 *
 * [edge] = warna tepi (terang), [core] = warna inti (lebih pekat), [glow] = cahaya bawah.
 */
enum class SeaGlassTint(
    val edgeLight: Color, val coreLight: Color, val glowLight: Color, val contentLight: Color,
    val edgeDark: Color, val coreDark: Color, val glowDark: Color, val contentDark: Color,
) {
    // Speaker aktif di referensi: biru langit -> cyan terang di bawah
    Sky(
        Color(0xFFB4E1FB), Color(0xFF8CCBF5), Color(0xFFC4FCFF), Color(0xFF16507C),
        Color(0xFF4FA3DA), Color(0xFF2F7DB8), Color(0xFF63D6F0), Color(0xFFEAF7FF),
    ),
    // Pill Mail/Meet di referensi: periwinkle
    Periwinkle(
        Color(0xFFBBCDF8), Color(0xFF8AA4F0), Color(0xFFCFE2FF), Color(0xFF1B2F7A),
        Color(0xFF7894E6), Color(0xFF516DC8), Color(0xFF93B4F5), Color(0xFFF0F4FF),
    ),
    // Tombol kirim / nav teal
    Aqua(
        Color(0xFF8EE0DA), Color(0xFF3DBBBD), Color(0xFFBFFFF7), Color(0xFF05424A),
        Color(0xFF44B5B6), Color(0xFF248B96), Color(0xFF6FE0D8), Color(0xFFE6FFFC),
    ),
    // Tombol akhiri panggilan di referensi
    Rose(
        Color(0xFFFFC2BC), Color(0xFFF59397), Color(0xFFFFD3CE), Color(0xFF7A2222),
        Color(0xFFDB7E88), Color(0xFFB4586A), Color(0xFFF2A5AC), Color(0xFFFFEDEB),
    ),
    Lime(
        Color(0xFFD7F7A6), Color(0xFF93E56E), Color(0xFFF1FFC9), Color(0xFF245A1D),
        Color(0xFF6DB84C), Color(0xFF47883A), Color(0xFF93D86F), Color(0xFFEBFFD6),
    ),
    Amber(
        Color(0xFFFFE6A8), Color(0xFFF5C26B), Color(0xFFFFF1CC), Color(0xFF734608),
        Color(0xFFC79134), Color(0xFF9A6A22), Color(0xFFE6B25D), Color(0xFFFFEFD1),
    ),
    Lilac(
        Color(0xFFE0D2FA), Color(0xFFC0A8F0), Color(0xFFF0E6FF), Color(0xFF45307E),
        Color(0xFF8D70C9), Color(0xFF6A52A8), Color(0xFFA68BDC), Color(0xFFF1E8FF),
    );

    fun edge(dark: Boolean) = if (dark) edgeDark else edgeLight
    fun core(dark: Boolean) = if (dark) coreDark else coreLight
    fun glow(dark: Boolean) = if (dark) glowDark else glowLight

    /** Warna isi permukaan datar gaya Kimi (tanpa gradasi). */
    fun fill(dark: Boolean): Color = when (this) {
        Sky -> if (dark) Color(0xFF1F3355) else Color(0xFFDCE8FF)
        Rose -> if (dark) Color(0xFF3A1F25) else Color(0xFFFFE3E6)
        else -> if (dark) Color(0xFF2A2A2A) else Color(0xFFECECF0)
    }

    /** Warna ikon/teks di atas permukaan. */
    fun content(dark: Boolean): Color = when (this) {
        Sky -> if (dark) Color(0xFF6FA2FF) else Color(0xFF1E54D6)
        Rose -> if (dark) Color(0xFFFF5A6A) else Color(0xFFD7263D)
        else -> if (dark) Color(0xFFEDEDED) else Color(0xFF111111)
    }

    fun shadow(dark: Boolean): Color = Color.Black
}

private fun DrawScope.paintOutline(outline: Outline, color: Color, style: DrawStyle = Fill) {
    when (outline) {
        is Outline.Rectangle -> drawRect(color, outline.rect.topLeft, outline.rect.size, style = style)
        is Outline.Rounded -> drawPath(Path().apply { addRoundRect(outline.roundRect) }, color, style = style)
        is Outline.Generic -> drawPath(outline.path, color, style = style)
    }
}

private fun borderColor(dark: Boolean) = if (dark) Color(0x2EFFFFFF) else Color(0x1A000000)

/** Tanpa bayangan: gaya Kimi datar. Dipertahankan agar pemanggil lama tetap kompatibel. */
fun Modifier.seaGlassShadow(shape: Shape, color: Color, elevation: Dp = 0.dp): Modifier = this

/**
 * Permukaan gaya Kimi: isi datar gelap/terang dengan garis tepi tipis (efek kaca "palsu"),
 * tanpa grain dan tanpa bayangan.
 */
fun Modifier.seaGlass(
    shape: Shape,
    tint: SeaGlassTint,
    dark: Boolean,
    elevation: Dp = 0.dp,
    grain: Boolean = false,
): Modifier = this
    .clip(shape)
    .drawBehind {
        val outline = shape.createOutline(size, layoutDirection, this)
        paintOutline(outline, tint.fill(dark))
        paintOutline(outline, borderColor(dark), Stroke(width = 1.5.dp.toPx()))
    }

fun Modifier.seaGlassColor(
    shape: Shape,
    top: Color,
    bottom: Color,
    shadowColor: Color,
    dark: Boolean,
    elevation: Dp = 0.dp,
): Modifier = this.clip(shape).drawBehind { drawRect(bottom) }

/** Permukaan non-aktif datar. */
fun Modifier.seaGlassFlat(
    shape: Shape,
    color: Color,
    dark: Boolean,
    elevation: Dp = 0.dp,
): Modifier = this.clip(shape).drawBehind { drawRect(color) }

/** Tidak ada grain di gaya Kimi. */
fun Modifier.seaGlassGrain(alpha: Float = 0f): Modifier = this

/** Lapisan isi di atas blur backdrop untuk input bar: gelap pekat dengan sedikit tembus. */
fun Modifier.seaGlassFrost(
    tint: SeaGlassTint,
    dark: Boolean,
    strength: Float = 0.9f,
): Modifier = this.drawBehind {
    drawRect(if (dark) Color(0xE61C1C1C) else Color(0xE6FFFFFF))
}

/** Fisika sentuh: menyusut dengan pegas lalu memantul kembali. */
@Composable
fun Modifier.seaGlassPress(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.94f,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
        label = "pressScale",
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/** Bentuk tetap pill bila aktif atau tidak (Kimi tidak mengubah bentuk tombol). */
@Composable
fun rememberSeaGlassMorphShape(active: Boolean): Shape = remember { RoundedCornerShape(50) }
