package me.rerere.ui.glass

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Liquid glass nyata (refraksi + dispersi) memakai shader AGSL dari AndroidLiquidGlassView
 * (MIT, QmDeve). Berbeda dari library aslinya yang membaca sebuah ViewGroup, versi ini merekam
 * isi di belakang kaca lewat GraphicsLayer Compose, sehingga aman dari rekursi: kaca tidak pernah
 * ikut terekam. Hanya aktif di Android 13+; di bawahnya jatuh ke permukaan datar gaya Kimi.
 */
@Stable
class LiquidBackdrop internal constructor(internal val layer: GraphicsLayer) {
    internal var origin by mutableStateOf(Offset.Zero)
}

val LocalLiquidBackdrop = staticCompositionLocalOf<LiquidBackdrop?> { null }

@Composable
fun rememberLiquidBackdrop(): LiquidBackdrop {
    val layer = rememberGraphicsLayer()
    return remember(layer) { LiquidBackdrop(layer) }
}

/** Tandai konten yang akan dibiaskan kaca (mis. daftar pesan). Kaca TIDAK boleh berada di dalamnya. */
fun Modifier.liquidBackdropSource(backdrop: LiquidBackdrop): Modifier = this
    .onGloballyPositioned { backdrop.origin = it.positionInWindow() }
    .drawWithContent {
        backdrop.layer.record { this@drawWithContent.drawContent() }
        drawLayer(backdrop.layer)
    }

/** Lapisan kaca cair di atas konten backdrop. Aman dipanggil di mana saja; tanpa backdrop = no-op. */
fun Modifier.liquidGlass(
    shape: Shape,
    alpha: Float = 0.72f,
    refractionHeight: Dp = 16.dp,
    refractionAmount: Dp = 42.dp,
    dispersion: Float = 0.3f,
): Modifier =
    if (Build.VERSION.SDK_INT >= 33) {
        this.liquidGlassApi33(shape, alpha, refractionHeight, refractionAmount, dispersion)
    } else {
        this
    }

@RequiresApi(33)
private fun Modifier.liquidGlassApi33(
    shape: Shape,
    alpha: Float,
    refractionHeight: Dp,
    refractionAmount: Dp,
    dispersion: Float,
): Modifier = composed {
    val backdrop = LocalLiquidBackdrop.current
    if (backdrop == null) {
        Modifier
    } else {
        val glass = rememberGraphicsLayer()
        val holder = remember { LiquidShaderHolder() }
        var position by remember { mutableStateOf(Offset.Zero) }
        Modifier
            .onGloballyPositioned { position = it.positionInWindow() }
            .drawWithContent {
                val w = size.width.toInt()
                val h = size.height.toInt()
                if (w > 0 && h > 0) {
                    val o = position - backdrop.origin
                    glass.record(size = IntSize(w, h)) {
                        translate(-o.x, -o.y) { drawLayer(backdrop.layer) }
                    }
                    val outline = shape.createOutline(size, layoutDirection, this)
                    val radius = when (outline) {
                        is Outline.Rounded -> outline.roundRect.topLeftCornerRadius.x
                        else -> 0f
                    }.coerceAtMost(minOf(size.width, size.height) / 2f)
                    glass.renderEffect = holder.effect(
                        w = size.width,
                        h = size.height,
                        radius = radius,
                        refractionHeight = refractionHeight.toPx(),
                        refractionAmount = -refractionAmount.toPx(),
                        dispersion = dispersion,
                    )
                    glass.alpha = alpha
                    drawLayer(glass)
                }
                drawContent()
            }
    }
}

@RequiresApi(33)
private class LiquidShaderHolder {
    private val shader = RuntimeShader(LIQUID_AGSL)

    fun effect(
        w: Float,
        h: Float,
        radius: Float,
        refractionHeight: Float,
        refractionAmount: Float,
        dispersion: Float,
    ): androidx.compose.ui.graphics.RenderEffect {
        shader.setFloatUniform("size", w, h)
        shader.setFloatUniform("offset", 0f, 0f)
        shader.setFloatUniform("cornerRadii", radius, radius, radius, radius)
        shader.setFloatUniform("refractionHeight", refractionHeight)
        shader.setFloatUniform("refractionAmount", refractionAmount)
        shader.setFloatUniform("depthEffect", 0.3f)
        shader.setFloatUniform("chromaticAberration", dispersion)
        shader.setFloatUniform("contrast", 0f)
        shader.setFloatUniform("whitePoint", 0f)
        shader.setFloatUniform("chromaMultiplier", 1f)
        shader.setFloatUniform("tintColor", 1f, 1f, 1f)
        shader.setFloatUniform("tintAlpha", 0f)
        return android.graphics.RenderEffect
            .createRuntimeShaderEffect(shader, "content")
            .asComposeRenderEffect()
    }
}

// Shader dari AndroidLiquidGlassView (MIT License, Copyright (c) 2025 QmDeve).
private const val LIQUID_AGSL = """
uniform shader content;
uniform float2 size;
uniform float2 offset;
uniform float4 cornerRadii;
uniform float refractionHeight;
uniform float refractionAmount;
uniform float depthEffect;
uniform float chromaticAberration;

uniform float contrast;
uniform float whitePoint;
uniform float chromaMultiplier;

uniform float3 tintColor;
uniform float tintAlpha;

const half3 rgbToY = half3(0.2126, 0.7152, 0.0722);

float radiusAt(float2 coord, float4 radii) {
    if (coord.x >= 0.0) {
        if (coord.y <= 0.0) return radii.y;
        else return radii.z;
    } else {
        if (coord.y <= 0.0) return radii.x;
        else return radii.w;
    }
}

float sdRoundedRect(float2 coord, float2 halfSize, float radius) {
    float2 cornerCoord = abs(coord) - (halfSize - float2(radius));
    float outside = length(max(cornerCoord, 0.0)) - radius;
    float inside = min(max(cornerCoord.x, cornerCoord.y), 0.0);
    return outside + inside;
}

float safeSign(float value) {
    return value < 0.0 ? -1.0 : 1.0;
}

float2 safeNormalize(float2 value, float2 fallback) {
    float len = length(value);
    if (len > 0.001) {
        return value / len;
    }
    return fallback;
}

float2 gradSdRoundedRect(float2 coord, float2 halfSize, float radius) {
    float2 cornerCoord = abs(coord) - (halfSize - float2(radius));
    if (cornerCoord.x >= 0.0 || cornerCoord.y >= 0.0) {
        float2 outside = max(cornerCoord, 0.0);
        float outsideLength = length(outside);
        if (outsideLength > 0.001) {
            return sign(coord) * (outside / outsideLength);
        }
        float useX = step(cornerCoord.y, cornerCoord.x);
        return float2(
            useX * safeSign(coord.x),
            (1.0 - useX) * safeSign(coord.y)
        );
    } else {
        float gradX = step(cornerCoord.y, cornerCoord.x);
        return sign(coord) * float2(gradX, 1.0 - gradX);
    }
}

float circleMap(float x) {
    return 1.0 - sqrt(1.0 - x * x);
}

half4 saturateColor(half4 color, float amount) {
    half3 lin = toLinearSrgb(color.rgb);
    float y = dot(lin, rgbToY);
    half3 gray = half3(y);
    half3 sat = fromLinearSrgb(mix(gray, lin, amount));
    return half4(sat, color.a);
}

half4 main(float2 coord) {
    float2 halfSize = size * 0.5;
    float2 centeredCoord = (coord + offset) - halfSize;
    float radius = radiusAt(centeredCoord, cornerRadii);

    float sd = sdRoundedRect(centeredCoord, halfSize, radius);
    if (-sd >= refractionHeight) {
        half4 baseColor = content.eval(coord);

        baseColor = saturateColor(baseColor, chromaMultiplier);
        float3 target = (whitePoint > 0.0) ? float3(1.0) : float3(0.0);
        baseColor.rgb = mix(baseColor.rgb, target, abs(whitePoint));
        baseColor.rgb = (baseColor.rgb - 0.5) * (1.0 + contrast) + 0.5;

        half3 tintedRGB = mix(baseColor.rgb, tintColor, tintAlpha);
        return half4(tintedRGB, baseColor.a);
    }

    sd = min(sd, 0.0);
    float d = circleMap(1.0 - -sd / refractionHeight) * refractionAmount;
    float smoothRadius = max(radius * 1.5, 30.0);
    float gradRadius = min(smoothRadius, min(halfSize.x, halfSize.y));

    float2 shapeGrad = gradSdRoundedRect(centeredCoord, halfSize, gradRadius);
    float2 depthGrad = safeNormalize(centeredCoord, shapeGrad);
    float2 grad = safeNormalize(shapeGrad + depthEffect * depthGrad, shapeGrad);

    float2 refractedCoord = coord + d * grad;
    float dispersionIntensity = chromaticAberration * ((centeredCoord.x * centeredCoord.y) / (halfSize.x * halfSize.y));
    float2 dispersedCoord = d * grad * dispersionIntensity;

    half4 color = half4(0.0);

    half4 red = content.eval(refractedCoord + dispersedCoord);
    color.r += red.r / 3.5;
    color.a += red.a / 7.0;

    half4 orange = content.eval(refractedCoord + dispersedCoord * (2.0 / 3.0));
    color.r += orange.r / 3.5;
    color.g += orange.g / 7.0;
    color.a += orange.a / 7.0;

    half4 yellow = content.eval(refractedCoord + dispersedCoord * (1.0 / 3.0));
    color.r += yellow.r / 3.5;
    color.g += yellow.g / 3.5;
    color.a += yellow.a / 7.0;

    half4 green = content.eval(refractedCoord);
    color.g += green.g / 3.5;
    color.a += green.a / 7.0;

    half4 cyan = content.eval(refractedCoord - dispersedCoord * (1.0 / 3.0));
    color.g += cyan.g / 3.5;
    color.b += cyan.b / 3.0;
    color.a += cyan.a / 7.0;

    half4 blue = content.eval(refractedCoord - dispersedCoord * (2.0 / 3.0));
    color.b += blue.b / 3.0;
    color.a += blue.a / 7.0;

    half4 purple = content.eval(refractedCoord - dispersedCoord);
    color.r += purple.r / 7.0;
    color.b += purple.b / 3.0;
    color.a += purple.a / 7.0;

    color = saturateColor(color, chromaMultiplier);
    float3 target = (whitePoint > 0.0) ? float3(1.0) : float3(0.0);
    color.rgb = mix(color.rgb, target, abs(whitePoint));
    color.rgb = (color.rgb - 0.5) * (1.0 + contrast) + 0.5;

    half3 tintedRGB = mix(color.rgb, tintColor, tintAlpha);
    return half4(tintedRGB, color.a);
}
"""
