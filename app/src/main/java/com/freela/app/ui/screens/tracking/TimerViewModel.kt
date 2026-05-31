package com.freela.app.ui.screens.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.SessioneLavoro
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.TimeTrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class TimerUiState(
    val sessioneAttiva: SessioneLavoro? = null,
    val clienteAttivo: Cliente? = null,
    val sessioniRecenti: List<SessioneLavoro> = emptyList(),
)

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timeRepo: TimeTrackingRepository,
    clienteRepo: ClienteRepository,
) : ViewModel() {

    val state: StateFlow<TimerUiState> = combine(
        timeRepo.osservaInCorso(),
        clienteRepo.osservaTutti(),
    ) { sessione, clienti ->
        val cliente = sessione?.let { s -> clienti.firstOrNull { it.id == s.clienteId } }
            ?: clienti.firstOrNull { it.faseCorrente == com.freela.app.domain.model.FasePipeline.IN_CORSO }
            ?: clienti.firstOrNull()
        TimerUiState(
            sessioneAttiva = sessione,
            clienteAttivo = cliente,
            sessioniRecenti = emptyList(), // PRD §11.4 fase 8 popolerà
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimerUiState())
}
