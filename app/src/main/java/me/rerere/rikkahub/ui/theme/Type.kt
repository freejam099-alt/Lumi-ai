package me.rerere.rikkahub.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.rerere.rikkahub.R

val base = Typography()
@OptIn(ExperimentalTextApi::class)
val SeaGlassFont = FontFamily(
    listOf(
        FontWeight.Light, FontWeight.Normal, FontWeight.Medium,
        FontWeight.SemiBold, FontWeight.Bold,
    ).map { w ->
        Font(
            resId = R.font.google_sans_flex,
            weight = w,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(w.weight),
                FontVariation.width(100f),
                ExtendedFontVariation.round(60),
            ),
        )
    }
)

// Google Sans Flex dipakai sebagai font default seluruh aplikasi
val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = SeaGlassFont),
    displayMedium = base.displayMedium.copy(fontFamily = SeaGlassFont),
    displaySmall = base.displaySmall.copy(fontFamily = SeaGlassFont),
    headlineLarge = base.headlineLarge.copy(fontFamily = SeaGlassFont),
    headlineMedium = base.headlineMedium.copy(fontFamily = SeaGlassFont),
    headlineSmall = base.headlineSmall.copy(fontFamily = SeaGlassFont),
    titleLarge = base.titleLarge.copy(fontFamily = SeaGlassFont),
    titleMedium = base.titleMedium.copy(fontFamily = SeaGlassFont),
    titleSmall = base.titleSmall.copy(fontFamily = SeaGlassFont),
    bodyLarge = base.bodyLarge.copy(fontFamily = SeaGlassFont),
    bodyMedium = base.bodyMedium.copy(fontFamily = SeaGlassFont),
    bodySmall = base.bodySmall.copy(fontFamily = SeaGlassFont),
    labelLarge = base.labelLarge.copy(fontFamily = SeaGlassFont),
    labelMedium = base.labelMedium.copy(fontFamily = SeaGlassFont),
    labelSmall = base.labelSmall.copy(fontFamily = SeaGlassFont),
    displayLargeEmphasized = base.displayLargeEmphasized.copy(fontFamily = SeaGlassFont),
    displayMediumEmphasized = base.displayMediumEmphasized.copy(fontFamily = SeaGlassFont),
    displaySmallEmphasized = base.displaySmallEmphasized.copy(fontFamily = SeaGlassFont),
    headlineLargeEmphasized = base.headlineLargeEmphasized.copy(fontFamily = SeaGlassFont),
    headlineMediumEmphasized = base.headlineMediumEmphasized.copy(fontFamily = SeaGlassFont),
    headlineSmallEmphasized = base.headlineSmallEmphasized.copy(fontFamily = SeaGlassFont),
    titleLargeEmphasized = base.titleLargeEmphasized.copy(fontFamily = SeaGlassFont),
    titleMediumEmphasized = base.titleMediumEmphasized.copy(fontFamily = SeaGlassFont),
    titleSmallEmphasized = base.titleSmallEmphasized.copy(fontFamily = SeaGlassFont),
    bodyLargeEmphasized = base.bodyLargeEmphasized.copy(fontFamily = SeaGlassFont),
    bodyMediumEmphasized = base.bodyMediumEmphasized.copy(fontFamily = SeaGlassFont),
    bodySmallEmphasized = base.bodySmallEmphasized.copy(fontFamily = SeaGlassFont),
    labelLargeEmphasized = base.labelLargeEmphasized.copy(fontFamily = SeaGlassFont),
    labelMediumEmphasized = base.labelMediumEmphasized.copy(fontFamily = SeaGlassFont),
    labelSmallEmphasized = base.labelSmallEmphasized.copy(fontFamily = SeaGlassFont),
)

// Set of Material typography styles to start with
//val Typography = Typography(
//    displayLargeEmphasized = base.displayLargeEmphasized.copy(
//        fontFamily = GoogleSansFlex.Display.Emphasized.Large,
//        fontWeight = FontWeight.Bold
//    ),
//    displayMediumEmphasized = base.displayMediumEmphasized.copy(
//        fontFamily = GoogleSansFlex.Display.Emphasized.Medium,
//        fontWeight = FontWeight.Bold
//    ),
//    displaySmallEmphasized = base.displaySmallEmphasized.copy(
//        fontFamily = GoogleSansFlex.Display.Emphasized.Large,
//        fontWeight = FontWeight.Bold
//    ),
//    headlineLargeEmphasized = base.headlineLargeEmphasized.copy(
//        fontFamily = GoogleSansFlex.Headline.Emphasized.Large,
//        fontWeight = FontWeight.Bold
//    ),
//    headlineMediumEmphasized = base.headlineMediumEmphasized.copy(
//        fontFamily = GoogleSansFlex.Headline.Emphasized.Medium,
//        fontWeight = FontWeight.Bold
//    ),
//    headlineSmallEmphasized = base.headlineSmallEmphasized.copy(
//        fontFamily = GoogleSansFlex.Headline.Emphasized.Large,
//        fontWeight = FontWeight.Bold
//    ),
//    titleLargeEmphasized = base.titleLargeEmphasized.copy(
//        fontFamily = GoogleSansFlex.Title.Emphasized.Large,
//        fontWeight = FontWeight.Bold
//    ),
//    titleMediumEmphasized = base.titleMediumEmphasized.copy(
//        fontFamily = GoogleSansFlex.Title.Emphasized.Medium,
//        fontWeight = FontWeight.Bold
//    ),
//    titleSmallEmphasized = base.titleSmallEmphasized.copy(
//        fontFamily = GoogleSansFlex.Title.Emphasized.Small,
//        fontWeight = FontWeight.Bold
//    ),
//    bodyLargeEmphasized = base.bodyLargeEmphasized.copy(
//        fontFamily = GoogleSansFlex.Body.Emphasized.Large,
//        fontWeight = FontWeight.Bold
//    ),
//    bodyMediumEmphasized = base.bodyMediumEmphasized.copy(
//        fontFamily = GoogleSansFlex.Body.Emphasized.Medium,
//        fontWeight = FontWeight.Bold
//    ),
//    bodySmallEmphasized = base.bodySmallEmphasized.copy(
//        fontFamily = GoogleSansFlex.Body.Emphasized.Small,
//        fontWeight = FontWeight.Bold
//    ),
//    labelLargeEmphasized = base.labelLargeEmphasized.copy(
//        fontFamily = GoogleSansFlex.Label.Emphasized.Large,
//        fontWeight = FontWeight.Bold
//    ),
//    labelMediumEmphasized = base.labelMediumEmphasized.copy(
//        fontFamily = GoogleSansFlex.Label.Emphasized.Medium,
//        fontWeight = FontWeight.Bold
//    ),
//    labelSmallEmphasized = base.labelSmallEmphasized.copy(
//        fontFamily = GoogleSansFlex.Label.Emphasized.Small,
//        fontWeight = FontWeight.Bold
//    ),
//)

@OptIn(ExperimentalTextApi::class)
val JetbrainsMono = FontFamily(
    Font(
        resId = R.font.jetbrains_mono,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Normal.weight),
        )
    )
)
