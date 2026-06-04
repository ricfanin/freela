package com.freela.app.ui.screens.boot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BootState(
    val ready: Boolean = false,
    val onboardingCompleted: Boolean = false,
)

/**
 * Decide la startDestination del NavHost in base alle preferenze utente (onboarding già fatto?).
 */
@HiltViewModel
class BootViewModel @Inject constructor(
    settings: SettingsRepository,
) : ViewModel() {

    val state: StateFlow<BootState> = settings.onboardingCompleted
        .map { BootState(ready = true, onboardingCompleted = it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BootState())
}
