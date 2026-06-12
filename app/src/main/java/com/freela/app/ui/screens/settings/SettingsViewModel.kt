package com.freela.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.data.seed.SeedDataSource
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TemaPreferito
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val tema: TemaPreferito = TemaPreferito.SISTEMA,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsRepository,
    private val seed: SeedDataSource,
) : ViewModel() {

    val state: StateFlow<SettingsUiState> = settings.temaPreferito
        .map { SettingsUiState(tema = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    /** Reset completo: svuota il database e azzera le preferenze, poi naviga all'onboarding. */
    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            seed.clear()
            settings.logout()
            onDone()
        }
    }

    fun cambiaTema(tema: TemaPreferito) {
        viewModelScope.launch { settings.impostaTema(tema) }
    }

    val notifScadenze: StateFlow<Boolean> =
        settings.notifScadenzeFatture.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val notifPromemoria: StateFlow<Boolean> =
        settings.notifPromemoriaClienti.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)
    val notifRiepilogo: StateFlow<Boolean> =
        settings.notifRiepilogoGiornaliero.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setNotifScadenze(v: Boolean) = viewModelScope.launch { settings.impostaNotifScadenzeFatture(v) }
    fun setNotifPromemoria(v: Boolean) = viewModelScope.launch { settings.impostaNotifPromemoriaClienti(v) }
    fun setNotifRiepilogo(v: Boolean) = viewModelScope.launch { settings.impostaNotifRiepilogoGiornaliero(v) }
}
