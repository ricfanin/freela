package com.freela.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TemaPreferito
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Espone la preferenza di tema a livello di Activity, così da pilotare [com.freela.app.ui.theme.FreelaTheme]
 * prima che venga composto il NavHost. DataStore è reattivo: al cambio tema dalle impostazioni la UI ricompone.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    settings: SettingsRepository,
) : ViewModel() {

    val tema: StateFlow<TemaPreferito> =
        settings.temaPreferito
            .stateIn(viewModelScope, SharingStarted.Eagerly, TemaPreferito.SISTEMA)
}
