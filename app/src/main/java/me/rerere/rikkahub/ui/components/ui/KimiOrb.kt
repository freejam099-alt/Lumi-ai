package me.rerere.rikkahub.ui.components.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/**
 * Orb biru bermata ala Kimi, dibuat hidup: bernapas (skala), melayang, memipih-memanjang
 * ringan, kilau bergeser, bola mata melirik dan berkedip. Mode [thinking]: cincin cahaya
 * berputar, mata menatap ke atas, dan denyut lebih cepat. Semua dibaca di fase gambar.
 */
@Composable
fun KimiOrb(modifier: Modifier = Modifier, size: Dp = 96.dp, thinking: Boolean = false) {
    val transition = rememberInfiniteTransition(label = "orb")
    val breath by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(if (thinking) 1100 else 3200, easing = androidx.compose.animation.core.LinearEasing)),
        label = "orbBreath",
    )
    val floatPhase by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4600, easing = androidx.compose.animation.core.LinearEasing)),
        label = "orbFloat",
    )
    val look by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbLook",
    )
    val spin by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1500, easing = androidx.compose.animation.core.LinearEasing)),
        label = "orbSpin",
    )
    val blink by transition.animateFloat(
        initialValue = 1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 3600
                1f at 0
                1f at 3100
                0.06f at 3220
                1f at 3380
                1f at 3420
                0.06f at 3500
                1f at 3600
            }
        ),
        label = "orbBlink",
    )
    Canvas(modifier = modifier.size(size)) {
        val base = this.size.minDimension / 2f * 0.86f
        val tau = 2f * PI.toFloat()
        val squash = sin(breath * tau) * 0.035f
        val rx = base * (1f + squash)
        val ry = base * (1f - squash)
        val dy = sin(floatPhase * tau) * base * 0.07f
        val c = Offset(this.size.width / 2f, this.size.height / 2f + dy)
        val glowR = base * (1.45f + 0.1f * sin(breath * tau))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x552F73FF), Color.Transparent),
                center = c, radius = glowR,
            ),
            radius = glowR, center = c,
        )
        drawOval(Color(0xFF16233D), topLeft = Offset(c.x - rx * 1.06f, c.y - ry * 1.06f), size = Size(rx * 2.12f, ry * 2.12f))
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF7AB0FF), Color(0xFF3A7DFF), Color(0xFF1B54E0)),
                center = Offset(c.x - rx * 0.25f, c.y - ry * 0.35f),
                radius = base * 1.25f,
            ),
            topLeft = Offset(c.x - rx, c.y - ry),
            size = Size(rx * 2f, ry * 2f),
        )
        // kilau yang bergeser pelan
        val hx = c.x - rx * (0.38f + 0.06f * look)
        drawCircle(Color.White.copy(alpha = 0.22f), radius = base * 0.26f, center = Offset(hx, c.y - ry * 0.48f))
        drawCircle(Color.White.copy(alpha = 0.10f), radius = base * 0.12f, center = Offset(c.x + rx * 0.42f, c.y + ry * 0.5f))
        if (thinking) {
            drawArc(
                brush = Brush.sweepGradient(listOf(Color.Transparent, Color(0xFF9CC4FF), Color.Transparent), center = c),
                startAngle = spin, sweepAngle = 110f, useCenter = false,
                topLeft = Offset(c.x - rx * 1.14f, c.y - ry * 1.14f),
                size = Size(rx * 2.28f, ry * 2.28f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = base * 0.07f, cap = androidx.compose.ui.graphics.StrokeCap.Round),
            )
        }
        // mata: pil vertikal; saat berpikir menatap ke atas
        val eyeW = base * 0.15f
        val eyeH = base * 0.32f * blink
        val gap = rx * 0.34f
        val ox = if (thinking) 0f else look * rx * 0.12f
        val oy = if (thinking) -ry * 0.16f else ry * 0.02f
        listOf(-gap, gap).forEach { dx ->
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(c.x + dx + ox - eyeW / 2f, c.y + oy - eyeH / 2f),
                size = Size(eyeW, eyeH),
                cornerRadius = CornerRadius(eyeW / 2f, eyeW / 2f),
            )
        }
    }
}

@Composable
fun KimiChip(icon: ImageVector, text: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
        }
    }
}

/** Halaman kosong chat: orb, sapaan, dan chip pintasan di atas input bar. */
@Composable
fun KimiEmptyState(
    greeting: String,
    chips: List<Triple<ImageVector, String, () -> Unit>>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            KimiOrb(size = 104.dp)
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            chips.forEach { (icon, label, onClick) -> KimiChip(icon, label, onClick) }
        }
    }
}
