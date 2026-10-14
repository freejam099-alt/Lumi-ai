package me.rerere.rikkahub.ui.components.ui

import androidx.compose.foundation.layout.fillMaxSize
import me.rerere.ui.glass.seaGlass
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.svg.css
import me.rerere.rikkahub.R
import me.rerere.rikkahub.ui.hooks.rememberAvatarShape
import me.rerere.rikkahub.ui.theme.LocalDarkMode
import me.rerere.rikkahub.utils.computeAIIconByName
import me.rerere.rikkahub.utils.toCssHex

@Composable
private fun AIIcon(
    path: String,
    name: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
) {
    val contentColor = LocalContentColor.current
    val context = LocalContext.current
    val model = remember(path, contentColor, context) {
        ImageRequest.Builder(context)
            .data("file:///android_asset/icons/$path")
            .css(
                """
                svg {
                  fill: ${contentColor.toCssHex()};
                }
            """.trimIndent()
            )
            .build()
    }
    Surface(
        modifier = modifier.size(24.dp),
        shape = rememberAvatarShape(loading),
        color = color,
    ) {
        AsyncImage(
            model = model,
            contentDescription = name,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun AutoAIIcon(
    name: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
) {
    if (name == "Open Search") {
        OpenSearchGlassIcon(modifier)
        return
    }
    val path = remember(name) { computeAIIconByName(name) } ?: run {
        TextAvatar(text = name, modifier = modifier, loading = loading, color = color)
        return
    }
    AIIcon(
        path = path,
        name = name,
        modifier = modifier,
        loading = loading,
        color = color,
    )
}

@Preview
@Composable
private fun PreviewAutoAIIcon() {
    Column {
        AutoAIIcon("测试")
    }
}

@Composable
fun SiliconFlowPowerByIcon(modifier: Modifier = Modifier) {
    val darkMode = LocalDarkMode.current
    if (!darkMode) {
        AsyncImage(model = R.drawable.siliconflow_light, contentDescription = null, modifier = modifier)
    } else {
        AsyncImage(model = R.drawable.siliconflow_dark, contentDescription = null, modifier = modifier)
    }
}

/** Ikon Open Search: squircle kaca aqua dengan globe + kaca pembesar. */
@Composable
private fun OpenSearchGlassIcon(modifier: Modifier) {
    val dark = me.rerere.rikkahub.ui.theme.LocalDarkMode.current
    val tint = me.rerere.ui.glass.SeaGlassTint.Aqua
    androidx.compose.foundation.layout.Box(
        modifier = modifier.then(
            Modifier.seaGlass(
                androidx.compose.foundation.shape.RoundedCornerShape(percent = 30),
                tint,
                dark,
                elevation = 4.dp,
            )
        ),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        androidx.compose.material3.Icon(
            imageVector = me.rerere.rikkahub.ui.theme.SeaIcons.OpenSearch,
            contentDescription = null,
            tint = tint.content(dark),
            modifier = Modifier.fillMaxSize(0.7f),
        )
    }
}
