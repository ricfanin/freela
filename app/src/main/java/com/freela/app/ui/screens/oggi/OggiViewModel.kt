package com.freela.app.ui.screens.oggi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.PersonaDemo
import com.freela.app.domain.model.SessioneLavoro
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.FinanzeRepository
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TimeTrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Home "Oggi" centrata sulla sessione di lavoro in corso (come da mockup Figma):
 * card timer in evidenza + riassunto finanziario del mese.
 */
data class OggiUiState(
    val persona: PersonaDemo? = null,
    val sessione: SessioneLavoro? = null,
    val clienteAttivo: Cliente? = null,
    val fatturato: Double = 0.0,
    val incassato: Double = 0.0,
    val attesi: Double = 0.0,
    val inRitardo: Double = 0.0,
    val numClienti: Int = 0,
    val oreMese: Float = 0f,
    val obiettivo: Double = 6000.0,
    val isLoading: Boolean = true,
)

@HiltViewModel
class OggiViewModel @Inject constructor(
    settings: SettingsRepository,
    clienteRepo: ClienteRepository,
    timeRepo: TimeTrackingRepository,
    finanzeRepo: FinanzeRepository,
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val startMese = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    private val endMese = Calendar.getInstance().apply {
        timeInMillis = startMese; add(Calendar.MONTH, 1)
    }.timeInMillis - 1

    private data class Base(
        val persona: PersonaDemo?,
        val sessione: SessioneLavoro?,
        val clienti: List<Cliente>,
        val incassato: Double,
        val attesi: Double,
    )

    val state: StateFlow<OggiUiState> = combine(
        settings.personaCorrente,
        timeRepo.osservaInCorso(),
        clienteRepo.osservaTutti(),
        finanzeRepo.osservaIncassatoPeriodo(startMese, endMese),
        finanzeRepo.osservaTotaleAttesi(now),
    ) { persona, sessione, clienti, incassato, attesi ->
        Base(persona, sessione, clienti, incassato, attesi)
    }.combine(finanzeRepo.osservaTotaleRitardo(now)) { b, ritardo ->
        val clienteAttivo = b.sessione?.let { s -> b.clienti.firstOrNull { it.id == s.clienteId } }
        OggiUiState(
            persona = b.persona,
            sessione = b.sessione,
            clienteAttivo = clienteAttivo,
            fatturato = b.incassato + b.attesi + ritardo,
            incassato = b.incassato,
            attesi = b.attesi,
            inRitardo = ritardo,
            numClienti = b.clienti.size,
            oreMese = ORE_MESE_PLACEHOLDER,
            obiettivo = 6000.0,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OggiUiState())

    private companion object {
        // V1: ore mese aggregate non ancora calcolate dalle sessioni reali (PRD §11.4).
        const val ORE_MESE_PLACEHOLDER = 47f
    }
}
