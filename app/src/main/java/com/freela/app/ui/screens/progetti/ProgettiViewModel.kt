package com.freela.app.ui.screens.progetti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.StatoProgetto
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.ProgettoRepository
import com.freela.app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProgettoUi(
    val id: Long,
    val clienteId: Long,
    val cliente: String,
    val nome: String,
    val stato: StatoProgetto,
    val percentuale: Int,
    val taskTotali: Int,
    val taskFatti: Int,
    val oreReali: Float,
    val orePrev: Float,
    val budget: Double,
)

data class ProgettiUiState(
    val aperti: List<ProgettoUi> = emptyList(),
    val completati: List<ProgettoUi> = emptyList(),
)

@HiltViewModel
class ProgettiViewModel @Inject constructor(
    progettoRepo: ProgettoRepository,
    clienteRepo: ClienteRepository,
    taskRepo: TaskRepository,
) : ViewModel() {

    val state: StateFlow<ProgettiUiState> = combine(
        progettoRepo.osservaTutti(),
        clienteRepo.osservaTutti(),
        taskRepo.osservaTutti(),
    ) { progetti, clienti, tasks ->
        val byId = clienti.associateBy { it.id }
        val ui = progetti.map { p ->
            val c = byId[p.clienteId]
            val taskCliente = tasks.filter { it.clienteId == p.clienteId }
            val tot = taskCliente.size
            val fatti = taskCliente.count { it.completato }
            val perc = when {
                tot > 0 -> fatti * 100 / tot
                p.stato == StatoProgetto.COMPLETATO -> 100
                p.stato == StatoProgetto.IN_CORSO -> 60
                else -> 0
            }
            val orePrev = p.oreStimate.toFloat()
            ProgettoUi(
                id = p.id,
                clienteId = p.clienteId,
                cliente = c?.nome ?: "—",
                nome = p.nome,
                stato = p.stato,
                percentuale = perc,
                taskTotali = tot,
                taskFatti = fatti,
                oreReali = orePrev * perc / 100f,
                orePrev = orePrev,
                budget = c?.importoPreventivato ?: 0.0,
            )
        }
        ProgettiUiState(
            aperti = ui.filter { it.stato != StatoProgetto.COMPLETATO },
            completati = ui.filter { it.stato == StatoProgetto.COMPLETATO },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgettiUiState())
}
