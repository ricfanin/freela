package com.freela.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.data.seed.SeedDataSource
import com.freela.app.domain.model.PersonaDemo
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TemaPreferito
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val personaCorrente: PersonaDemo? = null,
    val tema: TemaPreferito = TemaPreferito.SISTEMA,
    val reseedingPersona: PersonaDemo? = null,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsRepository,
    private val seed: SeedDataSource,
) : ViewModel() {

    private val _reseeding = MutableStateFlow<PersonaDemo?>(null)

    val state: StateFlow<SettingsUiState> = combine(
        settings.personaCorrente,
        settings.temaPreferito,
        _reseeding.asStateFlow(),
    ) { p, t, reseeding ->
        SettingsUiState(personaCorrente = p, tema = t, reseedingPersona = reseeding)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun cambiaPersona(persona: PersonaDemo) {
        viewModelScope.launch {
            _reseeding.value = persona
            seed.seed(persona)
            settings.impostaPersona(persona)
            _reseeding.value = null
        }
    }

    fun cambiaTema(tema: TemaPreferito) {
        viewModelScope.launch { settings.impostaTema(tema) }
    }
}
