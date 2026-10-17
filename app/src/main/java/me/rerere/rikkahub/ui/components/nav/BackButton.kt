package me.rerere.rikkahub.ui.components.nav

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import me.rerere.hugeicons.HugeIcons
import me.rerere.hugeicons.stroke.ArrowLeft01
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.context.LocalNavController
import me.rerere.ui.glass.GlassIconButton
import me.rerere.ui.glass.SeaGlassTint

@Composable
fun BackButton(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    GlassIconButton(
        onClick = { navController.popBackStack() },
        modifier = modifier,
        tint = SeaGlassTint.Periwinkle,
    ) {
        Icon(
            imageVector = HugeIcons.ArrowLeft01,
            contentDescription = stringResource(R.string.back)
        )
    }
}
