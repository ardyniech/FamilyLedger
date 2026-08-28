package com.example.shared.atoms

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// AGSL Shader for Glassmorphism & High-End Visuals
private const val SHADER_SRC = """
    uniform float2 resolution;
    uniform float time;
    
    vec4 main(in float2 fragCoord) {
        vec2 uv = fragCoord.xy / resolution.xy;
        float wave = sin(uv.x * 10.0 + time) * 0.5 + 0.5;
        vec3 col = vec3(0.05, 0.05, 0.1) + vec3(0.0, 0.1, 0.2) * wave;
        return vec4(col, 1.0);
    }
"""

@Composable
fun AnimatedMeshBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize().graphicsLayer {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val shader = RuntimeShader(SHADER_SRC)
            shader.setFloatUniform("resolution", size.width, size.height)
            shader.setFloatUniform("time", System.nanoTime() / 1_000_000_000f)
            renderEffect = RenderEffect.createRuntimeShaderEffect(shader, "content").asComposeRenderEffect()
        }
    }) {
        // Fallback for pre-Tiramisu
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A)),
                center = Offset(size.width / 2f, size.height / 3f),
                radius = size.width
            ),
            size = size
        )
    }
}
