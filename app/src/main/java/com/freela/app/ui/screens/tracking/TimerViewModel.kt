package com.freela.app.ui.screens.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.model.SessioneLavoro
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.TimeTrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TimerUiState(
    val sessioneAttiva: SessioneLavoro? = null,
    val clienteAttivo: Cliente? = null,
    val sessioniRecenti: List<SessioneLavoro> = emptyList(),
    val oreRealiMillis: Long = 0L,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timeRepo: TimeTrackingRepository,
    clienteRepo: ClienteRepository,
) : ViewModel() {

    private val base = combine(
        timeRepo.osservaInCorso(),
        clienteRepo.osservaTutti(),
    ) { sessione, clienti ->
        val cliente = sessione?.let { s -> clienti.firstOrNull { it.id == s.clienteId } }
            ?: clienti.firstOrNull { it.faseCorrente == FasePipeline.IN_CORSO }
            ?: clienti.firstOrNull()
        sessione to cliente
    }

    val state: StateFlow<TimerUiState> = base.flatMapLatest { (sessione, cliente) ->
        if (cliente == null) {
            flowOf(TimerUiState(sessioneAttiva = sessione, clienteAttivo = null))
        } else {
            combine(
                timeRepo.osservaPerCliente(cliente.id),
                timeRepo.osservaDurataTotaleMillis(cliente.id, System.currentTimeMillis()),
            ) { sessioni, durataMillis ->
                TimerUiState(
                    sessioneAttiva = sessione,
                    clienteAttivo = cliente,
                    sessioniRecenti = sessioni.filter { it.fine != null }.sortedByDescending { it.inizio }.take(5),
                    oreRealiMillis = durataMillis,
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimerUiState())

    /** Inserimento manuale di una sessione conclusa (PRD FR-19, US-17). */
    fun aggiungiManuale(clienteId: Long, minuti: Int, descrizione: String?) {
        if (minuti <= 0) return
        val fine = System.currentTimeMillis()
        val inizio = fine - minuti * 60_000L
        viewModelScope.launch {
            timeRepo.aggiungiManuale(
                SessioneLavoro(
                    clienteId = clienteId,
                    inizio = inizio,
                    fine = fine,
                    descrizione = descrizione,
                    inserimentoManuale = true,
                ),
            )
        }
    }
}
