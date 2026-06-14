package com.freela.app.ui.screens.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.model.Progetto
import com.freela.app.domain.model.SessioneLavoro
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.ProgettoRepository
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
    val oreRealiMillis: Long = 0L,
    val progettiCliente: List<Progetto> = emptyList(),
    val progettoAttivo: Progetto? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timeRepo: TimeTrackingRepository,
    clienteRepo: ClienteRepository,
    private val progettoRepo: ProgettoRepository,
) : ViewModel() {

    // se l'utente sceglie un cliente a mano, vince sull'auto-selezione
    private val clienteSelezionato = MutableStateFlow<Long?>(null)

    // null = nessun progetto, conta tutto il cliente
    private val progettoSelezionato = MutableStateFlow<Long?>(null)

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
                timeRepo.osservaDurataTotaleMillis(cliente.id, System.currentTimeMillis()),
                progettoRepo.osservaTutti(),
                progettoSelezionato,
            ) { durataMillis, tuttiProgetti, progSelId ->
                val progettiCliente = tuttiProgetti.filter { it.clienteId == cliente.id }
                // se il cliente ha un solo progetto lo seleziono da solo, se no rispetto la scelta
                val progettoAttivo = progettiCliente.firstOrNull { it.id == progSelId }
                    ?: progettiCliente.singleOrNull()
                TimerUiState(
                    sessioneAttiva = sessione,
                    clienteAttivo = cliente,
                    clienti = clienti,
                    oreRealiMillis = durataMillis,
                    progettiCliente = progettiCliente,
                    progettoAttivo = progettoAttivo,
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimerUiState())

    // cambio cliente: azzero il progetto, era di quello vecchio
    fun selezionaCliente(clienteId: Long) {
        clienteSelezionato.value = clienteId
        progettoSelezionato.value = null
    }

    fun selezionaProgetto(progettoId: Long?) { progettoSelezionato.value = progettoId }

    // inserimento a mano di una sessione già conclusa
    fun aggiungiManuale(clienteId: Long, progettoId: Long?, minuti: Int, descrizione: String?) {
        if (minuti <= 0) return
        val fine = System.currentTimeMillis()
        val inizio = fine - minuti * 60_000L
        viewModelScope.launch {
            timeRepo.aggiungiManuale(
                SessioneLavoro(
                    clienteId = clienteId,
                    progettoId = progettoId,
                    inizio = inizio,
                    fine = fine,
                    descrizione = descrizione,
                    inserimentoManuale = true,
                ),
            )
        }
    }
}
