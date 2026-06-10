package com.freela.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.data.seed.SeedDataSource
import com.freela.app.domain.model.PersonaDemo
import com.freela.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settings: SettingsRepository,
    private val seed: SeedDataSource,
) : ViewModel() {

    /**
     * Completa onboarding: setta flag, seedda Giulia se nessuna persona scelta.
     * Onclick "Inizia" della schermata.
     */
    fun completaOnboarding(
        nome: String = "",
        ruolo: String? = null,
        valuta: String = "EUR",
        onDone: () -> Unit,
    ) {
        viewModelScope.launch {
            val personaCorrente = settings.personaCorrente.first()
            if (personaCorrente == null) {
                seed.seed(PersonaDemo.GIULIA)
                settings.impostaPersona(PersonaDemo.GIULIA)
            }
            settings.impostaProfilo(nome, ruolo, valuta)
            settings.completaOnboarding()
            onDone()
        }
    }
}
