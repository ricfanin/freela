package com.freela.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.data.seed.SeedDataSource
import com.freela.app.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settings: SettingsRepository,
    private val seed: SeedDataSource,
) : ViewModel() {

    // carica il dataset demo, salva il profilo e segna l'onboarding come fatto
    fun completaOnboarding(
        nome: String = "",
        ruolo: String? = null,
        valuta: String = "EUR",
        onDone: () -> Unit,
    ) {
        viewModelScope.launch {
            seed.seed()
            settings.impostaProfilo(nome, ruolo, valuta)
            settings.completaOnboarding()
            onDone()
        }
    }
}
