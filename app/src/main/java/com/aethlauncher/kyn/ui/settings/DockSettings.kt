package com.aethlauncher.kyn.ui.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.aethlauncher.kyn.core.preferences.AppSettings
import com.aethlauncher.kyn.ui.components.SectionTitle
import com.aethlauncher.kyn.ui.components.StepperRow
import com.aethlauncher.kyn.ui.components.SwitchRow
import kotlin.math.roundToInt

@Composable
fun DockSettings(settings: AppSettings, viewModel: SettingsViewModel) {
    SectionTitle("Dock")
    StepperRow("Jumlah ikon", settings.dockCount, AppSettings.MIN_DOCK..AppSettings.MAX_DOCK, viewModel::setDockCount)
    DockSlider("Opasitas: ${settings.dockOpacityPercent}%", settings.dockOpacityPercent.toFloat(), 10f..100f) { viewModel.setDockOpacity(it) }
    DockSlider("Posisi X: ${settings.dockHorizontalPercent}%", settings.dockHorizontalPercent.toFloat(), 0f..100f) { viewModel.setDockHorizontal(it) }
    DockSlider("Posisi Y: ${settings.dockVerticalPercent}%", settings.dockVerticalPercent.toFloat(), 0f..100f) { viewModel.setDockVertical(it) }
    DockSlider("Ukuran: ${settings.dockScalePercent}%", settings.dockScalePercent.toFloat(), AppSettings.MIN_DOCK_SCALE.toFloat()..AppSettings.MAX_DOCK_SCALE.toFloat()) { viewModel.setDockScale(it) }
    SwitchRow("Tampilkan label di dock", checked = settings.showDockLabels, onCheckedChange = viewModel::setShowDockLabels)
    SwitchRow("Animasi interaksi", description = "Efek tekan spring dan transisi halus.", checked = settings.animationsEnabled, onCheckedChange = viewModel::setAnimationsEnabled)
}

@Composable
private fun DockSlider(title: String, initial: Float, range: ClosedFloatingPointRange<Float>, onFinished: (Int) -> Unit) {
    var value by remember(initial) { mutableFloatStateOf(initial) }
    Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
    Slider(value = value, onValueChange = { value = it }, onValueChangeFinished = { onFinished(value.roundToInt()) }, valueRange = range)
}
