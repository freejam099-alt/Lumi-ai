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
 * Orb biru bermata seperti maskot Kimi: melayang pelan, bola mata melirik ke samping,
 * dan berkedip beberapa detik sekali. Animasi dibaca di fase gambar saja (ringan).
 */
@Composable
fun KimiOrb(modifier: Modifier = Modifier, size: Dp = 96.dp) {
    val transition = rememberInfiniteTransition(label = "orb")
    val float by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3600, easing = androidx.compose.animation.core.LinearEasing)),
        label = "orbFloat",
    )
    val look by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "orbLook",
    )
    val blink by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            keyframes {
                durationMillis = 4200
                1f at 0
                1f at 3800
                0.08f at 3920
                1f at 4080
            }
        ),
        label = "orbBlink",
    )
    Canvas(modifier = modifier.size(size)) {
        val r = this.size.minDimension / 2f
        val dy = sin(float * 2f * PI.toFloat()) * r * 0.06f
        val center = Offset(this.size.width / 2f, this.size.height / 2f + dy)
        // cincin gelap tipis
        drawCircle(Color(0xFF1B2A44), radius = r, center = center)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF63A2FF), Color(0xFF2F73FF), Color(0xFF1F5BEA)),
                center = Offset(center.x - r * 0.2f, center.y - r * 0.3f),
                radius = r * 1.1f,
            ),
            radius = r * 0.92f,
            center = center,
        )
        // kilau
        drawCircle(Color.White.copy(alpha = 0.18f), radius = r * 0.28f, center = Offset(center.x - r * 0.35f, center.y - r * 0.45f))
        // mata
        val eyeW = r * 0.14f
        val eyeH = r * 0.30f * blink
        val gap = r * 0.36f
        val ox = look * r * 0.10f
        listOf(-gap, gap).forEach { dx ->
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(center.x + dx + ox - eyeW / 2f, center.y - r * 0.05f - eyeH / 2f),
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
