package com.freela.app.ui.screens.progetti

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.TaskRepository
import com.freela.app.domain.repository.TimeTrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ProgettoDetailState(
    val cliente: Cliente? = null,
    val nomeProgetto: String = "",
    val tasks: List<Task> = emptyList(),
    val oreRealiMillis: Long = 0L,
)

@HiltViewModel
class ProgettoDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    clienteRepo: ClienteRepository,
    taskRepo: TaskRepository,
    timeRepo: TimeTrackingRepository,
) : ViewModel() {

    private val clienteId: Long = savedStateHandle.get<Long>("clienteId") ?: 0L
    private val now = System.currentTimeMillis()

    private val nomiProgetto = listOf(
        "Sito e-commerce", "Brand identity", "App mobile", "Sito vetrina",
        "Campagna social", "Restyling logo", "Landing page", "Identità visiva",
    )

    val state: StateFlow<ProgettoDetailState> = combine(
        clienteRepo.osservaTutti(),
        taskRepo.osservaTutti(),
        timeRepo.osservaDurataTotaleMillis(clienteId, now),
    ) { clienti, tasks, durata ->
        val c = clienti.firstOrNull { it.id == clienteId }
        ProgettoDetailState(
            cliente = c,
            nomeProgetto = nomiProgetto[(clienteId % nomiProgetto.size).toInt()],
            tasks = tasks.filter { it.clienteId == clienteId },
            oreRealiMillis = durata,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProgettoDetailState())
}
