package com.aethlauncher.kyn.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aethlauncher.kyn.core.AppContainer
import com.aethlauncher.kyn.core.preferences.AppSettings
import com.aethlauncher.kyn.core.recovery.EmergencyExitResult
import com.aethlauncher.kyn.core.recovery.RecoveryDecision
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val container: AppContainer) : ViewModel() {

    val recoveryState: StateFlow<RecoveryDecision> = container.crashRecoveryManager.state

    /** Null sampai pengaturan selesai dibaca dari DataStore. */
    val settings: StateFlow<AppSettings?> = container.settingsRepository.settings
        .map<AppSettings, AppSettings?> { it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun onStartupCompleted() {
        container.crashRecoveryManager.markStartupCompleted()
    }

    fun enableSafeModeAndContinue() {
        viewModelScope.launch { container.settingsRepository.setSafeMode(true) }
        container.crashRecoveryManager.clearHistory()
    }

    fun continueNormally() {
        container.crashRecoveryManager.clearHistory()
    }

    fun openEmergencyExit(context: Context): EmergencyExitResult =
        container.emergencyRecoveryController.open(context)

    fun updateDockPosition(horizontal: Int, vertical: Int) {
        viewModelScope.launch {
            container.settingsRepository.update {
                it.copy(
                    dockHorizontalPercent = horizontal.coerceIn(0, 100),
                    dockVerticalPercent = vertical.coerceIn(0, 100)
                )
            }
        }
    }

    fun updateDockScale(scale: Int) {
        viewModelScope.launch {
            container.settingsRepository.update {
                it.copy(dockScalePercent = scale.coerceIn(AppSettings.MIN_DOCK_SCALE, AppSettings.MAX_DOCK_SCALE))
            }
        }
    }

    fun updateClockPosition(horizontal: Int, vertical: Int) {
        viewModelScope.launch {
            container.settingsRepository.update {
                it.copy(clockHorizontalPercent = horizontal.coerceIn(0, 100), clockVerticalPercent = vertical.coerceIn(0, 100))
            }
        }
    }
}
