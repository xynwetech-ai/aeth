package com.aethlauncher.kyn.core.theme

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * Tema Aether di atas Material 3.
 *
 * Safe Mode memakai warna standar sistem: tanpa dynamic color dan tanpa gaya khusus.
 * AMOLED Dark selalu gelap dengan background hitam murni.
 */
@Composable
fun AetherTheme(
    style: ThemeStyle = ThemeStyle.TRANSPARENT,
    mode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    safeMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val policy = RenderPolicy.forSafeMode(safeMode)
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()

    val dark = when {
        safeMode -> systemDark
        style == ThemeStyle.AMOLED_DARK -> true
        mode == ThemeMode.DARK -> true
        mode == ThemeMode.LIGHT -> false
        else -> systemDark
    }

    val useDynamic = policy.dynamicColorEnabled && dynamicColor &&
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    var colorScheme: ColorScheme = when {
        useDynamic -> if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> darkColorScheme()
        else -> lightColorScheme()
    }

    if (!safeMode && style == ThemeStyle.AMOLED_DARK) {
        colorScheme = colorScheme.copy(
            background = Color.Black,
            surface = Color.Black,
            surfaceVariant = Color(0xFF1A1A1A)
        )
    }

    NativeGlassWindowEffect(enabled = style == ThemeStyle.NATIVE_GLASS && !safeMode)
    CompositionLocalProvider(
        LocalRenderPolicy provides policy,
        LocalVisualStyle provides VisualStyle(style, safeMode)
    ) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}

@Composable
private fun NativeGlassWindowEffect(enabled: Boolean) {
    val view = LocalView.current
    val window = (view.context as? Activity)?.window
    DisposableEffect(window, enabled) {
        if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (enabled) {
                window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                window.setBackgroundBlurRadius(80)
            } else {
                window.setBackgroundBlurRadius(0)
                window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            }
        }
        onDispose {
            if (window != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window.setBackgroundBlurRadius(0)
                window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            }
        }
    }
}
