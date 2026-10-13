package me.rerere.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import me.rerere.ui.glass.SeaGlassTint
import me.rerere.ui.glass.rememberSeaGlassMorphShape
import me.rerere.ui.glass.seaGlass
import me.rerere.ui.glass.seaGlassPress

/**
 * Sea Glass: saat aktif menjadi squircle kaca biru berbutir dengan bayangan berwarna,
 * saat tidak aktif tetap datar dan transparan (seperti Keypad/Mute di referensi).
 */
@Composable
fun ToggleSurface(
    checked: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val interaction = remember { MutableInteractionSource() }
    val actualShape = shape ?: rememberSeaGlassMorphShape(checked)
    val glass = if (checked) {
        Modifier.seaGlass(actualShape, SeaGlassTint.Sky, dark, elevation = 8.dp)
    } else Modifier
    val contentColor =
        if (checked) SeaGlassTint.Sky.content(dark) else MaterialTheme.colorScheme.onSurface
    Surface(
        onClick = onClick,
        interactionSource = interaction,
        color = Color.Transparent,
        contentColor = contentColor,
        modifier = modifier
            .seaGlassPress(interaction, pressedScale = 0.9f)
            .then(glass),
        shape = actualShape,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        ProvideTextStyle(MaterialTheme.typography.labelLarge) {
            content()
        }
    }
}
