package me.rerere.ui.glass

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Tombol ikon kaca melayang (pill/lingkaran, atau squircle bila [shape] diberikan). */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: SeaGlassTint = SeaGlassTint.Periwinkle,
    size: Dp = 44.dp,
    shape: Shape? = null,
    content: @Composable () -> Unit,
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val interaction = remember { MutableInteractionSource() }
    val actualShape = shape ?: RoundedCornerShape(50)
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .seaGlassPress(interaction)
            .size(size)
            .seaGlass(actualShape, tint, dark, elevation = 9.dp)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick),
    ) {
        CompositionLocalProvider(LocalContentColor provides tint.content(dark)) { content() }
    }
}

/** Wadah pill kaca melayang untuk beberapa tombol (seperti pill Mail/Meet di referensi). */
@Composable
fun GlassPill(
    modifier: Modifier = Modifier,
    tint: SeaGlassTint = SeaGlassTint.Periwinkle,
    content: @Composable RowScope.() -> Unit,
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .height(44.dp)
            .defaultMinSize(minWidth = 44.dp)
            .seaGlass(RoundedCornerShape(50), tint, dark, elevation = 9.dp)
            .padding(horizontal = 4.dp),
    ) {
        CompositionLocalProvider(LocalContentColor provides tint.content(dark)) { content() }
    }
}
