package com.freela.app.ui.screens.storico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.FinanzeRepository
import com.freela.app.domain.repository.InterazioneRepository
import com.freela.app.domain.repository.TimeTrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ClienteAttenzione(val cliente: Cliente, val ore: Float)

data class StoricoUiState(
    val totOre: Float = 0f,
    val totInterazioni: Int = 0,
    val totIncassato: Double = 0.0,
    val distribuzione: List<ClienteAttenzione> = emptyList(),
    val topClienti: List<Cliente> = emptyList(),
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class StoricoViewModel @Inject constructor(
    clienteRepo: ClienteRepository,
    timeRepo: TimeTrackingRepository,
    interazioneRepo: InterazioneRepository,
    finanzeRepo: FinanzeRepository,
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val unMeseFa = now - 30L * 86400000L

    val state: StateFlow<StoricoUiState> = clienteRepo.osservaTutti()
        .flatMapLatest { clienti ->
            // Per ogni cliente, durata totale.
            val flows = clienti.map { c ->
                timeRepo.osservaDurataTotaleMillis(c.id, now).map { millis ->
                    ClienteAttenzione(c, millis / 1000f / 60f / 60f)
                }
            }
            if (flows.isEmpty()) flowOf(emptyList())
            else combine(flows) { it.toList() }
        }
        .combine(finanzeRepo.osservaIncassatoPeriodo(unMeseFa, now)) { distribuzione, incassato ->
            StoricoUiState(
                totOre = distribuzione.sumOf { it.ore.toDouble() }.toFloat(),
                totInterazioni = 0,
                totIncassato = incassato,
                distribuzione = distribuzione.sortedByDescending { it.ore },
                topClienti = distribuzione
                    .filter { it.cliente.importoPreventivato != null }
                    .sortedByDescending { it.cliente.importoPreventivato ?: 0.0 }
                    .take(4)
                    .map { it.cliente },
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StoricoUiState())
}
