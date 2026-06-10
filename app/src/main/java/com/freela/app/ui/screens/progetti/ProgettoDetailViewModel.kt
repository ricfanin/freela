package com.freela.app.ui.screens.progetti

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.Progetto
import com.freela.app.domain.model.StatoProgetto
import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.ProgettoRepository
import com.freela.app.domain.repository.TaskRepository
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

data class ProgettoDetailState(
    val progetto: Progetto? = null,
    val cliente: Cliente? = null,
    val tasks: List<Task> = emptyList(),
    val oreRealiMillis: Long = 0L,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProgettoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val progettoRepo: ProgettoRepository,
    private val clienteRepo: ClienteRepository,
    private val taskRepo: TaskRepository,
    private val timeRepo: TimeTrackingRepository,
) : ViewModel() {

    private val progettoId: Long = savedStateHandle.get<Long>("progettoId") ?: 0L
    private val now = System.currentTimeMillis()

    val state: StateFlow<ProgettoDetailState> = progettoRepo.osserva(progettoId)
        .flatMapLatest { progetto ->
            if (progetto == null) {
                flowOf(ProgettoDetailState())
            } else {
                combine(
                    clienteRepo.osserva(progetto.clienteId),
                    taskRepo.osservaTutti(),
                    timeRepo.osservaDurataTotaleMillis(progetto.clienteId, now),
                ) { cliente, tasks, durata ->
                    ProgettoDetailState(
                        progetto = progetto,
                        cliente = cliente,
                        tasks = tasks.filter { it.clienteId == progetto.clienteId },
                        oreRealiMillis = durata,
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgettoDetailState())

    /** Marca/smarca come preferito il cliente collegato al progetto. */
    fun cambiaPreferitoCliente() {
        val cliente = state.value.cliente ?: return
        viewModelScope.launch { clienteRepo.cambiaPreferito(cliente.id, !cliente.preferito) }
    }

    fun cambiaStato(stato: StatoProgetto) {
        val progetto = state.value.progetto ?: return
        viewModelScope.launch { progettoRepo.aggiorna(progetto.copy(stato = stato)) }
    }

    fun elimina(onDone: () -> Unit) {
        if (progettoId == 0L) return
        viewModelScope.launch {
            progettoRepo.elimina(progettoId)
            onDone()
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            if (task.completato) {
                taskRepo.aggiorna(task.copy(completato = false, dataCompletamento = null))
            } else {
                taskRepo.completa(task.id)
            }
        }
    }
}
