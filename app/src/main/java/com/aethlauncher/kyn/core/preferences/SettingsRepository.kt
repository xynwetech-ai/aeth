package com.aethlauncher.kyn.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aethlauncher.kyn.core.theme.ThemeMode
import com.aethlauncher.kyn.core.theme.ThemeStyle
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.aetherDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "aether_settings"
)

private object Keys {
    // Key SAFE_MODE sama dengan Milestone 1 agar pengaturan lama tetap terbaca.
    val SAFE_MODE = booleanPreferencesKey("safe_mode_enabled")
    val THEME_STYLE = stringPreferencesKey("theme_style")
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    val GRID_COLUMNS = intPreferencesKey("grid_columns")
    val GRID_ROWS = intPreferencesKey("grid_rows")
    val SHOW_LABELS = booleanPreferencesKey("show_labels")
    val DEFAULT_PAGE = intPreferencesKey("default_page")
    val DOCK_COUNT = intPreferencesKey("dock_count")
    val DOCK_OPACITY = intPreferencesKey("dock_opacity_percent")
    val DOCK_HORIZONTAL = intPreferencesKey("dock_horizontal_percent")
    val DOCK_VERTICAL = intPreferencesKey("dock_vertical_percent")
    val DOCK_SCALE = intPreferencesKey("dock_scale_percent")
    val SHOW_DOCK_LABELS = booleanPreferencesKey("show_dock_labels")
    val ANIMATIONS = booleanPreferencesKey("animations_enabled")
    val CLOCK_HORIZONTAL = intPreferencesKey("clock_horizontal_percent")
    val CLOCK_VERTICAL = intPreferencesKey("clock_vertical_percent")
    val SHOW_CLOCK = booleanPreferencesKey("show_clock")
    val SHOW_DATE = booleanPreferencesKey("show_date")
}

class SettingsRepository(context: Context) {

    private val dataStore = context.applicationContext.aetherDataStore

    // Data korup atau gagal dibaca jatuh ke default, bukan crash.
    val settings: Flow<AppSettings> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs -> prefs.toSettings() }

    val safeModeEnabled: Flow<Boolean> = settings.map { it.safeMode }

    suspend fun update(transform: (AppSettings) -> AppSettings) {
        dataStore.edit { prefs -> prefs.write(transform(prefs.toSettings())) }
    }

    suspend fun setSafeMode(enabled: Boolean) {
        update { it.copy(safeMode = enabled) }
    }
}

private fun Preferences.toSettings(): AppSettings {
    val d = AppSettings()
    val styleName = this[Keys.THEME_STYLE]
    val modeName = this[Keys.THEME_MODE]
    return AppSettings(
        safeMode = this[Keys.SAFE_MODE] ?: d.safeMode,
        themeStyle = when (styleName) {
            "LIQUID_GLASS" -> ThemeStyle.TRANSPARENT
            else -> enumValues<ThemeStyle>().firstOrNull { it.name == styleName } ?: d.themeStyle
        },
        themeMode = enumValues<ThemeMode>().firstOrNull { it.name == modeName } ?: d.themeMode,
        dynamicColor = this[Keys.DYNAMIC_COLOR] ?: d.dynamicColor,
        gridColumns = (this[Keys.GRID_COLUMNS] ?: d.gridColumns)
            .coerceIn(AppSettings.MIN_COLUMNS, AppSettings.MAX_COLUMNS),
        gridRows = (this[Keys.GRID_ROWS] ?: d.gridRows)
            .coerceIn(AppSettings.MIN_ROWS, AppSettings.MAX_ROWS),
        showLabels = this[Keys.SHOW_LABELS] ?: d.showLabels,
        defaultPage = (this[Keys.DEFAULT_PAGE] ?: d.defaultPage).coerceAtLeast(0),
        dockCount = (this[Keys.DOCK_COUNT] ?: d.dockCount)
            .coerceIn(AppSettings.MIN_DOCK, AppSettings.MAX_DOCK),
        dockOpacityPercent = (this[Keys.DOCK_OPACITY] ?: d.dockOpacityPercent)
            .coerceIn(AppSettings.MIN_OPACITY, AppSettings.MAX_OPACITY),
        dockHorizontalPercent = (this[Keys.DOCK_HORIZONTAL] ?: d.dockHorizontalPercent).coerceIn(0, 100),
        dockVerticalPercent = (this[Keys.DOCK_VERTICAL] ?: d.dockVerticalPercent).coerceIn(0, 100),
        dockScalePercent = (this[Keys.DOCK_SCALE] ?: d.dockScalePercent)
            .coerceIn(AppSettings.MIN_DOCK_SCALE, AppSettings.MAX_DOCK_SCALE),
        showDockLabels = this[Keys.SHOW_DOCK_LABELS] ?: d.showDockLabels,
        animationsEnabled = this[Keys.ANIMATIONS] ?: d.animationsEnabled,
        clockHorizontalPercent = (this[Keys.CLOCK_HORIZONTAL] ?: d.clockHorizontalPercent).coerceIn(0, 100),
        clockVerticalPercent = (this[Keys.CLOCK_VERTICAL] ?: d.clockVerticalPercent).coerceIn(0, 100),
        showClock = this[Keys.SHOW_CLOCK] ?: d.showClock,
        showDate = this[Keys.SHOW_DATE] ?: d.showDate
    )
}

private fun MutablePreferences.write(s: AppSettings) {
    this[Keys.SAFE_MODE] = s.safeMode
    this[Keys.THEME_STYLE] = s.themeStyle.name
    this[Keys.THEME_MODE] = s.themeMode.name
    this[Keys.DYNAMIC_COLOR] = s.dynamicColor
    this[Keys.GRID_COLUMNS] = s.gridColumns.coerceIn(AppSettings.MIN_COLUMNS, AppSettings.MAX_COLUMNS)
    this[Keys.GRID_ROWS] = s.gridRows.coerceIn(AppSettings.MIN_ROWS, AppSettings.MAX_ROWS)
    this[Keys.SHOW_LABELS] = s.showLabels
    this[Keys.DEFAULT_PAGE] = s.defaultPage.coerceAtLeast(0)
    this[Keys.DOCK_COUNT] = s.dockCount.coerceIn(AppSettings.MIN_DOCK, AppSettings.MAX_DOCK)
    this[Keys.DOCK_OPACITY] = s.dockOpacityPercent
        .coerceIn(AppSettings.MIN_OPACITY, AppSettings.MAX_OPACITY)
    this[Keys.DOCK_HORIZONTAL] = s.dockHorizontalPercent.coerceIn(0, 100)
    this[Keys.DOCK_VERTICAL] = s.dockVerticalPercent.coerceIn(0, 100)
    this[Keys.DOCK_SCALE] = s.dockScalePercent.coerceIn(AppSettings.MIN_DOCK_SCALE, AppSettings.MAX_DOCK_SCALE)
    this[Keys.SHOW_DOCK_LABELS] = s.showDockLabels
    this[Keys.ANIMATIONS] = s.animationsEnabled
    this[Keys.CLOCK_HORIZONTAL] = s.clockHorizontalPercent.coerceIn(0, 100)
    this[Keys.CLOCK_VERTICAL] = s.clockVerticalPercent.coerceIn(0, 100)
    this[Keys.SHOW_CLOCK] = s.showClock
    this[Keys.SHOW_DATE] = s.showDate
}
