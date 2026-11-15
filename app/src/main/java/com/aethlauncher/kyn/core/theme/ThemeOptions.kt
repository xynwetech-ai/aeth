package com.aethlauncher.kyn.core.theme

enum class ThemeStyle {
    TRANSPARENT,
    NATIVE_GLASS,
    ULTRA_MINIMAL,
    AMOLED_DARK,
    SOFT_LIGHT
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

/** Gaya visual yang aktif, dibaca oleh Home Screen untuk memilih background dan Dock. */
data class VisualStyle(
    val style: ThemeStyle,
    val safeMode: Boolean
)
