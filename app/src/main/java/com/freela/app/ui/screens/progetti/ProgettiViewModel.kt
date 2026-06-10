package com.freela.app.ui.screens.progetti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Area Progetti — UI derivata (PRD: non esiste un modello "Progetto" persistito,
 * i progetti sono ricostruiti dai clienti con lavoro preventivato + relativi task).
 */
enum class StatoProgetto { IN_CORSO, DA_INIZIARE, COMPLETATO }

data class ProgettoUi(
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
    clienteRepo: ClienteRepository,
    taskRepo: TaskRepository,
) : ViewModel() {

    val state: StateFlow<ProgettiUiState> = combine(
        clienteRepo.osservaTutti(),
        taskRepo.osservaTutti(),
    ) { clienti, tasks ->
        val progetti = clienti
            .filter { it.importoPreventivato != null }
            .map { c -> buildProgetto(c, tasks.filter { it.clienteId == c.id }) }
        ProgettiUiState(
            aperti = progetti.filter { it.stato != StatoProgetto.COMPLETATO },
            completati = progetti.filter { it.stato == StatoProgetto.COMPLETATO },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgettiUiState())

    private fun buildProgetto(c: Cliente, taskCliente: List<Task>): ProgettoUi {
        val stato = when (c.faseCorrente) {
            FasePipeline.CONSEGNATO, FasePipeline.IN_ATTESA_PAGAMENTO,
            FasePipeline.CHIUSO, FasePipeline.CLIENTE_RICORRENTE -> StatoProgetto.COMPLETATO
            FasePipeline.IN_CORSO -> StatoProgetto.IN_CORSO
            else -> StatoProgetto.DA_INIZIARE
        }
        val tot = taskCliente.size
        val fatti = taskCliente.count { it.completato }
        val perc = when {
            tot > 0 -> fatti * 100 / tot
            stato == StatoProgetto.COMPLETATO -> 100
            stato == StatoProgetto.IN_CORSO -> 60
            else -> 0
        }
        val orePrev = c.orePreventivate ?: 0f
        return ProgettoUi(
            clienteId = c.id,
            cliente = c.nome,
            nome = NOMI_PROGETTO[(c.id % NOMI_PROGETTO.size).toInt()],
            stato = stato,
            percentuale = perc,
            taskTotali = tot,
            taskFatti = fatti,
            oreReali = orePrev * perc / 100f,
            orePrev = orePrev,
            budget = c.importoPreventivato ?: 0.0,
        )
    }

    private companion object {
        val NOMI_PROGETTO = listOf(
            "Sito e-commerce", "Brand identity", "App mobile", "Sito vetrina",
            "Campagna social", "Restyling logo", "Landing page", "Identità visiva",
        )
    }
}
