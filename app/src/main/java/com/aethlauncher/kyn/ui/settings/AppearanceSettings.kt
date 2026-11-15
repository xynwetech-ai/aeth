package com.aethlauncher.kyn.ui.settings

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import com.aethlauncher.kyn.core.preferences.AppSettings
import com.aethlauncher.kyn.core.theme.ThemeMode
import com.aethlauncher.kyn.core.theme.ThemeStyle
import com.aethlauncher.kyn.ui.components.ChoiceChips
import com.aethlauncher.kyn.ui.components.SectionTitle
import com.aethlauncher.kyn.ui.components.SwitchRow

@Composable
fun AppearanceSettings(settings: AppSettings, viewModel: SettingsViewModel) {
    if (settings.safeMode) {
        Text(
            text = "Safe Mode aktif: warna standar sistem dipakai, pilihan tema di bawah belum berlaku.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }

    SectionTitle("Gaya tema")
    ChoiceChips(
        options = ThemeStyle.entries.toList(),
        selected = settings.themeStyle,
        label = ::styleLabel,
        onSelect = viewModel::setThemeStyle
    )

    SectionTitle("Mode")
    ChoiceChips(
        options = ThemeMode.entries.toList(),
        selected = settings.themeMode,
        label = ::modeLabel,
        onSelect = viewModel::setThemeMode
    )
    Text(
        text = "Pilih gaya. Ketuk kartu untuk melihat preview mini; Native Glass memakai blur sistem bila perangkat mendukung.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    ThemePreviewRow(settings, viewModel)
    if (settings.themeStyle == ThemeStyle.AMOLED_DARK) {
        Text(
            text = "AMOLED Dark selalu memakai mode gelap.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    val dynamicSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    SwitchRow(
        title = "Warna dinamis (Material You)",
        description = if (dynamicSupported) {
            "Warna mengikuti wallpaper."
        } else {
            "Butuh Android 12 atau lebih baru."
        },
        checked = settings.dynamicColor && dynamicSupported,
        onCheckedChange = viewModel::setDynamicColor,
        enabled = dynamicSupported
    )
}

private fun styleLabel(style: ThemeStyle): String = when (style) {
    ThemeStyle.TRANSPARENT -> "Transparent"
    ThemeStyle.NATIVE_GLASS -> "Native Glass"
    ThemeStyle.ULTRA_MINIMAL -> "Ultra Minimal"
    ThemeStyle.AMOLED_DARK -> "AMOLED Dark"
    ThemeStyle.SOFT_LIGHT -> "Soft Light"
}

private fun modeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> "Ikuti sistem"
    ThemeMode.LIGHT -> "Terang"
    ThemeMode.DARK -> "Gelap"
}

@Composable
private fun ThemePreviewRow(settings: AppSettings, viewModel: SettingsViewModel) {
    androidx.compose.foundation.layout.Row(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth().horizontalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
    ) {
        ThemeStyle.entries.forEach { style ->
            androidx.compose.material3.Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
                color = previewColor(style),
                tonalElevation = if (settings.themeStyle == style) 5.dp else 1.dp,
                modifier = androidx.compose.ui.Modifier.width(108.dp).height(72.dp).clickable { viewModel.setThemeStyle(style) }
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = androidx.compose.ui.Modifier.padding(10.dp),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Bottom
                ) {
                    Text(styleLabel(style), style = MaterialTheme.typography.labelMedium, color = previewTextColor(style))
                    Text(if (style == ThemeStyle.NATIVE_GLASS) "API 31+" else "Preview", style = MaterialTheme.typography.labelSmall, color = previewTextColor(style).copy(alpha = .7f))
                }
            }
        }
    }
}

private fun previewColor(style: ThemeStyle) = when (style) {
    ThemeStyle.TRANSPARENT -> Color(0xFF4A5568)
    ThemeStyle.NATIVE_GLASS -> Color(0xFF26384A)
    ThemeStyle.ULTRA_MINIMAL -> Color(0xFF111318)
    ThemeStyle.AMOLED_DARK -> Color.Black
    ThemeStyle.SOFT_LIGHT -> Color(0xFFE8EAF0)
}

private fun previewTextColor(style: ThemeStyle) = if (style == ThemeStyle.SOFT_LIGHT) Color(0xFF202124) else Color.White
