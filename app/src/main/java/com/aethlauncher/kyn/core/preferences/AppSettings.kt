package com.aethlauncher.kyn.core.preferences

import com.aethlauncher.kyn.core.theme.ThemeMode
import com.aethlauncher.kyn.core.theme.ThemeStyle

data class AppSettings(
    val safeMode: Boolean = false,
    val themeStyle: ThemeStyle = ThemeStyle.TRANSPARENT,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val gridColumns: Int = 4,
    val gridRows: Int = 6,
    val showLabels: Boolean = true,
    val defaultPage: Int = 0,
    val dockCount: Int = 4,
    val dockOpacityPercent: Int = 60,
    val dockHorizontalPercent: Int = 50,
    val dockVerticalPercent: Int = 88,
    val dockScalePercent: Int = 100,
    val showDockLabels: Boolean = false,
    val animationsEnabled: Boolean = true,
    val clockHorizontalPercent: Int = 50,
    val clockVerticalPercent: Int = 8,
    val showClock: Boolean = true,
    val showDate: Boolean = true
) {
    companion object {
        const val MIN_COLUMNS = 3
        const val MAX_COLUMNS = 6
        const val MIN_ROWS = 3
        const val MAX_ROWS = 8
        const val MIN_DOCK = 3
        const val MAX_DOCK = 5
        const val MIN_OPACITY = 10
        const val MAX_OPACITY = 100
        const val MIN_DOCK_SCALE = 75
        const val MAX_DOCK_SCALE = 125

        /** Preset grid dalam format (kolom, baris). */
        val GRID_PRESETS = listOf(4 to 5, 4 to 6, 5 to 5, 5 to 6, 5 to 7)
    }
}
