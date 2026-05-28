package com.freela.app.ui.screens.clienti

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.Fattura
import com.freela.app.domain.model.Interazione
import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.FinanzeRepository
import com.freela.app.domain.repository.InterazioneRepository
import com.freela.app.domain.repository.TaskRepository
import com.freela.app.domain.repository.TimeTrackingRepository
import com.freela.app.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class ClienteDetailUiState(
    val cliente: Cliente? = null,
    val timeline: List<Interazione> = emptyList(),
    val prossimoTask: Task? = null,
    val fatture: List<Fattura> = emptyList(),
    val oreReali: Float = 0f,
    val isLoading: Boolean = true,
)

@HiltViewModel
class ClienteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    clienteRepo: ClienteRepository,
    interazioneRepo: InterazioneRepository,
    taskRepo: TaskRepository,
    timeRepo: TimeTrackingRepository,
    finanzeRepo: FinanzeRepository,
) : ViewModel() {

    private val clienteId: Long = savedStateHandle[Routes.ARG_CLIENTE_ID] ?: 0L
    private val now = System.currentTimeMillis()

    val state: StateFlow<ClienteDetailUiState> = if (clienteId == 0L) {
        flowOf(ClienteDetailUiState(isLoading = false))
    } else {
        combine(
            clienteRepo.osserva(clienteId),
            interazioneRepo.osservaPerCliente(clienteId),
            taskRepo.osservaProssimoPerCliente(clienteId),
            finanzeRepo.osservaFatturePerCliente(clienteId),
            timeRepo.osservaDurataTotaleMillis(clienteId, now),
        ) { c, timeline, task, fatture, durataMillis ->
            ClienteDetailUiState(
                cliente = c,
                timeline = timeline,
                prossimoTask = task,
                fatture = fatture,
                oreReali = (durataMillis / 1000f / 60f / 60f),
                isLoading = false,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClienteDetailUiState())
}
