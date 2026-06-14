package com.freela.app.ui.screens.progetti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.Progetto
import com.freela.app.domain.model.StatoProgetto
import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.ProgettoRepository
import com.freela.app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NuovoProgettoViewModel @Inject constructor(
    clienteRepo: ClienteRepository,
    private val progettoRepo: ProgettoRepository,
    private val taskRepo: TaskRepository,
) : ViewModel() {

    val clienti: StateFlow<List<Cliente>> = clienteRepo.osservaTutti()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun salva(
        clienteId: Long,
        nome: String,
        oreStimate: Int,
        deadline: Long?,
        taskTitoli: List<String>,
        onDone: () -> Unit,
    ) {
        if (clienteId == 0L || nome.isBlank()) return
        val scadenzaTask = deadline ?: (System.currentTimeMillis() + 7L * 86_400_000L)
        viewModelScope.launch {
            progettoRepo.crea(
                Progetto(
                    clienteId = clienteId,
                    nome = nome.trim(),
                    deadline = deadline,
                    oreStimate = oreStimate,
                    stato = StatoProgetto.DA_INIZIARE,
                ),
            )
            // i task del progetto li creo come task del cliente collegato
            taskTitoli.filter { it.isNotBlank() }.forEach { titolo ->
                taskRepo.crea(
                    Task(
                        titolo = titolo.trim(),
                        clienteId = clienteId,
                        scadenza = scadenzaTask,
                    ),
                )
            }
            onDone()
        }
    }
}
