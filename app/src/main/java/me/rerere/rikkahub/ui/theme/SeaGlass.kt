package me.rerere.rikkahub.ui.theme

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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Random

/**
 * Sea Glass (Material 4 concept).
 *
 * Prinsip materialnya:
 *  1. Warna tembus cahaya dengan gradasi (terang di atas/kiri, pekat di bawah/kanan), tanpa border.
 *  2. Grain halus seperti permukaan kaca laut yang terabrasi (frosted).
 *  3. Bayangan berwarna (bukan hitam) yang jatuh ke bawah, memberi kesan melayang.
 *  4. Rim-light lembut di bagian atas sebagai pengganti outline.
 *  5. Tombol aktif berubah bentuk (pill -> squircle) dengan fisika pegas.
 */
enum class SeaGlassTint(
    val lightTop: Color,
    val lightBottom: Color,
    val darkTop: Color,
    val darkBottom: Color,
    val lightContent: Color,
    val darkContent: Color,
) {
    Aqua(
        Color(0xFFC9FBF7), Color(0xFF6FD9D0), Color(0xFF3FA7A0), Color(0xFF1E6F78),
        Color(0xFF0B4F55), Color(0xFFD7FFFB),
    ),
    Periwinkle(
        Color(0xFFCFE6FF), Color(0xFF9DB6F5), Color(0xFF5F7FD6), Color(0xFF3A4FA3),
        Color(0xFF263B82), Color(0xFFE3EBFF),
    ),
    Sky(
        Color(0xFFC9FBFF), Color(0xFF9ED7FA), Color(0xFF4E9ACB), Color(0xFF2B6694),
        Color(0xFF1C5A82), Color(0xFFDDF3FF),
    ),
    Rose(
        Color(0xFFFFD7D7), Color(0xFFF7A3A8), Color(0xFFC96A74), Color(0xFF8E3F4D),
        Color(0xFF8A2E2E), Color(0xFFFFE1E1),
    ),
    Lime(
        Color(0xFFF2FFC4), Color(0xFF8FE36B), Color(0xFF6BB84A), Color(0xFF3F7F32),
        Color(0xFF245A1D), Color(0xFFEBFFD6),
    ),
    Amber(
        Color(0xFFFFEFC2), Color(0xFFF5C26B), Color(0xFFC79134), Color(0xFF8A5E1E),
        Color(0xFF7A4B0E), Color(0xFFFFEFD1),
    ),
    Lilac(
        Color(0xFFEBDDFF), Color(0xFFC3A8F0), Color(0xFF8D70C9), Color(0xFF5B4496),
        Color(0xFF45307E), Color(0xFFF1E8FF),
    );

    fun top(dark: Boolean) = if (dark) darkTop else lightTop
    fun bottom(dark: Boolean) = if (dark) darkBottom else lightBottom
    fun content(dark: Boolean) = if (dark) darkContent else lightContent
}

/** Bitmap noise kecil yang di-tile, dibuat sekali saja (murah di GPU). */
private val GrainBrush: ShaderBrush by lazy {
    val size = 96
    val rnd = Random(7)
    val px = IntArray(size * size)
    for (i in px.indices) {
        if (rnd.nextFloat() < 0.16f) {
            val alpha = 38 + rnd.nextInt(90)
            val light = rnd.nextBoolean()
            val c = if (light) 255 else 40
            px[i] = (alpha shl 24) or (c shl 16) or (c shl 8) or (if (light) 255 else 70)
        }
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
    ambientColor = color.copy(alpha = 0.55f),
    spotColor = color.copy(alpha = 0.85f),
)

/**
 * Permukaan kaca laut berwarna: gradasi + grain + rim-light. Tanpa border.
 * Konten (ikon/teks) digambar di atasnya sehingga tetap tajam.
 */
fun Modifier.seaGlass(
    shape: Shape,
    tint: SeaGlassTint,
    dark: Boolean,
    elevation: Dp = 10.dp,
    grain: Boolean = true,
): Modifier = this
    .seaGlassShadow(shape, tint.bottom(dark), elevation)
    .clip(shape)
    .drawBehind {
        drawRect(
            Brush.linearGradient(
                colors = listOf(tint.top(dark), tint.bottom(dark)),
                start = Offset(0f, 0f),
                end = Offset(size.width * 0.55f, size.height),
            )
        )
        // Rim-light lembut di atas (pengganti border)
        drawRect(
            Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = if (dark) 0.16f else 0.42f), Color.Transparent),
                endY = size.height * 0.5f,
            )
        )
        if (grain) drawRect(GrainBrush, alpha = if (dark) 0.5f else 0.7f)
    }

/** Versi dengan warna sendiri (misal diturunkan dari ColorScheme) untuk permukaan netral. */
fun Modifier.seaGlassColor(
    shape: Shape,
    top: Color,
    bottom: Color,
    shadowColor: Color,
    dark: Boolean,
    elevation: Dp = 8.dp,
): Modifier = this
    .seaGlassShadow(shape, shadowColor, elevation)
    .clip(shape)
    .drawBehind {
        drawRect(
            Brush.linearGradient(
                colors = listOf(top, bottom),
                start = Offset(0f, 0f),
                end = Offset(size.width * 0.55f, size.height),
            )
        )
        drawRect(
            Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = if (dark) 0.10f else 0.35f), Color.Transparent),
                endY = size.height * 0.5f,
            )
        )
        drawRect(GrainBrush, alpha = if (dark) 0.35f else 0.5f)
    }

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

/** Bentuk yang berubah dari pill ke squircle saat aktif/ditekan, dengan pegas. */
@Composable
fun rememberSeaGlassMorphShape(active: Boolean): Shape {
    val percent by animateFloatAsState(
        targetValue = if (active) 32f else 50f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "seaGlassMorph",
    )
    return remember(percent) { RoundedCornerShape(percent = percent.toInt().coerceIn(0, 50)) }
}
