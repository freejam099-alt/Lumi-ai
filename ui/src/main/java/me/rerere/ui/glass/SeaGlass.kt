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
    fun content(dark: Boolean) = if (dark) contentDark else contentLight

    /** Bayangan biru-abu yang diwarnai tint, seperti di referensi. */
    fun shadow(dark: Boolean): Color =
        lerp(core(dark), if (dark) Color(0xFF05070D) else Color(0xFF3C4C6A), 0.55f)
}

/** Grain terrazzo: bintik abu/putih 2px yang tidak beraturan, di-tile sekali saja. */
private val GrainBrush: ShaderBrush by lazy {
    val size = 128
    val rnd = Random(11)
    val px = IntArray(size * size)
    fun put(x: Int, y: Int, argb: Int) {
        if (x in 0 until size && y in 0 until size) px[y * size + x] = argb
    }
    repeat(size * size / 14) {
        val x = rnd.nextInt(size)
        val y = rnd.nextInt(size)
        val alpha = 46 + rnd.nextInt(96)
        val light = rnd.nextFloat() < 0.38f
        val argb = if (light) {
            (alpha shl 24) or 0xFFFFFF
        } else {
            (alpha shl 24) or 0x7C8CA6
        }
        put(x, y, argb)
        if (rnd.nextBoolean()) put(x + 1, y, argb)
        if (rnd.nextFloat() < 0.35f) put(x, y + 1, argb)
    }
    val bmp = Bitmap.createBitmap(px, size, size, Bitmap.Config.ARGB_8888)
    ShaderBrush(ImageShader(bmp.asImageBitmap(), TileMode.Repeated, TileMode.Repeated))
}

/** Bayangan berwarna yang jatuh ke bawah. Harus dipasang SEBELUM clip/background. */
fun Modifier.seaGlassShadow(
    shape: Shape,
    color: Color,
    elevation: Dp = 10.dp,
): Modifier = this.shadow(
    elevation = elevation,
    shape = shape,
    clip = false,
    ambientColor = color.copy(alpha = 0.50f),
    spotColor = color.copy(alpha = 0.90f),
)

/** Isi kaca: tepi terang, inti pekat lembut (blotch), cahaya bawah, rim-light, grain. */
private fun Modifier.glassFill(
    edge: Color, core: Color, glow: Color, dark: Boolean, grain: Float, alpha: Float = 1f,
): Modifier = this.drawBehind {
    val w = size.width
    val h = size.height
    drawRect(edge.copy(alpha = alpha))
    // inti lebih pekat, lembut ke arah tepi
    drawRect(
        Brush.radialGradient(
            colors = listOf(core.copy(alpha = 0.98f * alpha), core.copy(alpha = 0.6f * alpha), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.42f),
            radius = maxOf(w, h) * 0.62f,
        )
    )
    // cahaya di bagian bawah (cyan/terang)
    drawRect(
        Brush.verticalGradient(
            colors = listOf(Color.Transparent, glow.copy(alpha = (if (dark) 0.45f else 0.8f) * alpha)),
            startY = h * 0.45f,
            endY = h,
        )
    )
    // rim-light lembut di atas
    drawRect(
        Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = if (dark) 0.14f else 0.38f), Color.Transparent),
            endY = h * 0.35f,
        )
    )
    drawRect(GrainBrush, alpha = grain)
}

/** Permukaan kaca laut berwarna. Tanpa border. */
fun Modifier.seaGlass(
    shape: Shape,
    tint: SeaGlassTint,
    dark: Boolean,
    elevation: Dp = 10.dp,
    grain: Boolean = true,
): Modifier = this
    .seaGlassShadow(shape, tint.shadow(dark), elevation)
    .clip(shape)
    .glassFill(tint.edge(dark), tint.core(dark), tint.glow(dark), dark, if (grain) 1f else 0f)

/** Kaca dengan warna sendiri, mis. diturunkan dari ColorScheme (ikon Settings). */
fun Modifier.seaGlassColor(
    shape: Shape,
    top: Color,
    bottom: Color,
    shadowColor: Color,
    dark: Boolean,
    elevation: Dp = 8.dp,
): Modifier = this
    .seaGlassShadow(shape, lerp(shadowColor, if (dark) Color(0xFF05070D) else Color(0xFF3C4C6A), 0.45f), elevation)
    .clip(shape)
    .glassFill(top, bottom, lerp(top, Color.White, 0.5f), dark, 0.8f)

/** Permukaan non-aktif: putih datar, tanpa tekstur, bayangan biru-abu tipis (Keypad/Mute di referensi). */
fun Modifier.seaGlassFlat(
    shape: Shape,
    color: Color,
    dark: Boolean,
    elevation: Dp = 3.dp,
): Modifier = this
    .seaGlassShadow(shape, if (dark) Color(0xFF05070D) else Color(0xFF8DA0C0), elevation)
    .clip(shape)
    .drawBehind { drawRect(color) }

/** Fisika sentuh: menyusut dengan pegas lalu memantul kembali. */
@Composable
fun Modifier.seaGlassPress(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.92f,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "seaGlassPressScale",
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/** Bentuk yang berubah dari pill ke squircle saat aktif, dengan pegas. */
@Composable
fun rememberSeaGlassMorphShape(active: Boolean): Shape {
    val percent by animateFloatAsState(
        targetValue = if (active) 32f else 50f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "seaGlassMorph",
    )
    return remember(percent) { RoundedCornerShape(percent = percent.toInt().coerceIn(0, 50)) }
}

/**
 * Lapisan kaca tembus pandang di atas blur backdrop (depth of field): konten di belakang
 * tetap terbaca samar, sementara warna, grain, dan rim-light kaca laut tetap terlihat jelas.
 */
fun Modifier.seaGlassFrost(
    tint: SeaGlassTint,
    dark: Boolean,
    strength: Float = 0.62f,
): Modifier = this.glassFill(
    tint.edge(dark), tint.core(dark), tint.glow(dark), dark, grain = 0.85f, alpha = strength,
)
