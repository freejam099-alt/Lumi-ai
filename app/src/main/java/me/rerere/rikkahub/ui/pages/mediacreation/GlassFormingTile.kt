package me.rerere.rikkahub.ui.pages.mediacreation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import me.rerere.mediagen.model.MediaKind
import me.rerere.rikkahub.data.model.MediaCreationRecord
import me.rerere.rikkahub.data.model.MediaCreationStatus
import me.rerere.ui.glass.SeaGlassTint
import me.rerere.ui.glass.seaGlassGrain
import me.rerere.ui.glass.seaGlassShadow
import java.time.Duration
import java.time.Instant
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animasi "kotak terbentuk": saat server mulai membuat gambar/video, sebuah orb kaca kecil
 * membesar dengan pegas menjadi kotak bersudut halus (tanpa border) pada rasio yang diminta
 * (atau otomatis). Di dalamnya blob warna Sea Glass bergerak pelan, ada sapuan cahaya dan grain.
 * Tahap dari server (menyiapkan, antre, membuat, mengunduh) ditampilkan sebagai indikator tahap.
 */
@Composable
internal fun GlassFormingTile(record: MediaCreationRecord, modifier: Modifier = Modifier) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val isVideo = record.kind == MediaKind.VIDEO
    val targetRatio = remember(record.params.aspectRatio, record.params.resolution, isVideo) {
        resolveRatio(record.params.aspectRatio, record.params.resolution, isVideo)
    }

    // 1) Muncul: membesar dari orb kecil, sudut menghalus dari bulat ke 28dp
    val formed = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        formed.animateTo(1f, spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessLow))
    }
    // 2) Rasio berubah halus saat otomatis -> final atau saat parameter berganti
    val ratio by animateFloatAsState(
        targetValue = targetRatio,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow),
        label = "formingRatio",
    )
    // 3) Fase gerak (dibaca hanya di fase gambar, tidak memicu recomposition)
    val transition = rememberInfiniteTransition(label = "forming")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "formingPhase",
    )
    val sweep by transition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "formingSweep",
    )
    val elapsed by produceState(initialValue = elapsedSeconds(record.createAt), record.createAt) {
        while (true) {
            value = elapsedSeconds(record.createAt)
            delay(1000)
        }
    }

    val tints = if (isVideo) {
        listOf(SeaGlassTint.Periwinkle, SeaGlassTint.Lilac, SeaGlassTint.Sky)
    } else {
        listOf(SeaGlassTint.Sky, SeaGlassTint.Aqua, SeaGlassTint.Periwinkle)
    }
    val shape = RoundedCornerShape(lerpDp(80f, 28f, formed.value).dp)

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxWidth(lerpF(0.35f, 1f, formed.value))
                .aspectRatio(ratio.coerceIn(0.4f, 2.4f))
                .graphicsLayer { alpha = formed.value.coerceIn(0f, 1f) }
                .seaGlassShadow(shape, tints[0].shadow(dark), elevation = 12.dp)
                .clip(shape)
                .drawBehind {
                    val w = size.width
                    val h = size.height
                    drawRect(tints[0].edge(dark))
                    val t = phase * 2f * PI.toFloat()
                    // tiga blob yang bergerak pelan (Lissajous)
                    tints.forEachIndexed { i, tint ->
                        val k = i + 1
                        val cx = w * (0.5f + 0.32f * sin(t * k * 0.5f + i * 2.1f))
                        val cy = h * (0.5f + 0.30f * cos(t * (k * 0.4f + 0.3f) + i * 1.3f))
                        drawRect(
                            Brush.radialGradient(
                                colors = listOf(
                                    tint.core(dark).copy(alpha = 0.95f),
                                    tint.core(dark).copy(alpha = 0.35f),
                                    Color.Transparent,
                                ),
                                center = Offset(cx, cy),
                                radius = maxOf(w, h) * 0.55f,
                            )
                        )
                    }
                    // cahaya bawah + rim atas
                    drawRect(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, tints[0].glow(dark).copy(alpha = if (dark) 0.35f else 0.7f)),
                            startY = h * 0.5f,
                            endY = h,
                        )
                    )
                    // sapuan cahaya diagonal
                    val sx = w * sweep
                    drawRect(
                        Brush.linearGradient(
                            colors = listOf(Color.Transparent, Color.White.copy(alpha = if (dark) 0.12f else 0.35f), Color.Transparent),
                            start = Offset(sx - w * 0.25f, 0f),
                            end = Offset(sx + w * 0.25f, h),
                        )
                    )
                }
                .seaGlassGrain(0.9f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = record.status.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = tints[0].content(dark),
                    textAlign = TextAlign.Center,
                )
                StageDots(status = record.status, tint = tints[0], dark = dark)
                val count = record.params.count ?: 1
                Text(
                    text = buildString {
                        append("%d:%02d".format(elapsed / 60, elapsed % 60))
                        if (count > 1) append("  ·  ×").append(count)
                        record.params.aspectRatio?.takeIf { it.isNotBlank() }?.let { append("  ·  ").append(it) }
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = tints[0].content(dark).copy(alpha = 0.8f),
                )
            }
        }
    }
}

/** Empat tahap dari server: menyiapkan, antre, membuat, mengunduh. */
@Composable
private fun StageDots(status: MediaCreationStatus, tint: SeaGlassTint, dark: Boolean) {
    val current = when (status) {
        MediaCreationStatus.PREPARING -> 0
        MediaCreationStatus.QUEUED -> 1
        MediaCreationStatus.RUNNING -> 2
        MediaCreationStatus.DOWNLOADING -> 3
        else -> 3
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(4) { i ->
            val size by animateFloatAsState(
                targetValue = if (i == current) 12f else 8f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium),
                label = "stageDot",
            )
            Box(
                Modifier
                    .size(size.dp)
                    .clip(CircleShape)
                    .drawBehind {
                        drawRect(tint.content(dark).copy(alpha = if (i <= current) 0.9f else 0.28f))
                    }
            )
        }
    }
}

private fun elapsedSeconds(since: Instant): Long =
    Duration.between(since, Instant.now()).seconds.coerceAtLeast(0)

private fun lerpF(a: Float, b: Float, t: Float) = a + (b - a) * t.coerceIn(0f, 1.2f)
private fun lerpDp(a: Float, b: Float, t: Float) = a + (b - a) * t.coerceIn(0f, 1f)

/** "16:9" -> 1.777; "1536x1024" -> 1.5; kosong/auto -> 1:1 untuk gambar, 16:9 untuk video. */
private fun resolveRatio(aspect: String?, resolution: String?, isVideo: Boolean): Float {
    fun parse(text: String?, sep: Regex): Float? {
        val m = text?.let { sep.find(it) } ?: return null
        val w = m.groupValues[1].toFloatOrNull() ?: return null
        val h = m.groupValues[2].toFloatOrNull() ?: return null
        return if (w > 0f && h > 0f) w / h else null
    }
    return parse(aspect, Regex("""(\d+(?:\.\d+)?)\s*[:/]\s*(\d+(?:\.\d+)?)"""))
        ?: parse(resolution, Regex("""(\d+)\s*[xX*]\s*(\d+)"""))
        ?: if (isVideo) 16f / 9f else 1f
}
