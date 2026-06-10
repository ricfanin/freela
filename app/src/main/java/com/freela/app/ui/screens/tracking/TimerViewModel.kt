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
import kotlinx.coroutines.flow.MutableStateFlow
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
    val clienti: List<Cliente> = emptyList(),
    val sessioniRecenti: List<SessioneLavoro> = emptyList(),
    val oreRealiMillis: Long = 0L,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timeRepo: TimeTrackingRepository,
    clienteRepo: ClienteRepository,
) : ViewModel() {

    /** Cliente scelto manualmente dall'utente; ha precedenza sull'auto-selezione. */
    private val clienteSelezionato = MutableStateFlow<Long?>(null)

    private val base = combine(
        timeRepo.osservaInCorso(),
        clienteRepo.osservaTutti(),
        clienteSelezionato,
    ) { sessione, clienti, selId ->
        val cliente = sessione?.let { s -> clienti.firstOrNull { it.id == s.clienteId } }
            ?: selId?.let { id -> clienti.firstOrNull { it.id == id } }
            ?: clienti.firstOrNull { it.faseCorrente == FasePipeline.IN_CORSO }
            ?: clienti.firstOrNull()
        Triple(sessione, cliente, clienti)
    }

    val state: StateFlow<TimerUiState> = base.flatMapLatest { (sessione, cliente, clienti) ->
        if (cliente == null) {
            flowOf(TimerUiState(sessioneAttiva = sessione, clienteAttivo = null, clienti = clienti))
        } else {
            combine(
                timeRepo.osservaPerCliente(cliente.id),
                timeRepo.osservaDurataTotaleMillis(cliente.id, System.currentTimeMillis()),
            ) { sessioni, durataMillis ->
                TimerUiState(
                    sessioneAttiva = sessione,
                    clienteAttivo = cliente,
                    clienti = clienti,
                    sessioniRecenti = sessioni.filter { it.fine != null }.sortedByDescending { it.inizio },
                    oreRealiMillis = durataMillis,
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimerUiState())

    fun selezionaCliente(clienteId: Long) { clienteSelezionato.value = clienteId }

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

    fun eliminaSessione(sessioneId: Long) {
        viewModelScope.launch { timeRepo.elimina(sessioneId) }
    }
}
