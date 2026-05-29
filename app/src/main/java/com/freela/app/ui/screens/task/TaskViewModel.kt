package com.freela.app.ui.screens.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.OrigineTask
import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskGruppo { OGGI, SETTIMANA, SUGGERITI }

data class TaskRiga(val task: Task, val cliente: Cliente?, val gruppo: TaskGruppo)
data class TaskUiState(
    val righe: List<TaskRiga> = emptyList(),
    val totaleAperti: Int = 0,
    val totaleUrgenti: Int = 0,
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepo: TaskRepository,
    clienteRepo: ClienteRepository,
) : ViewModel() {

    val state: StateFlow<TaskUiState> = combine(
        taskRepo.osservaAperti(),
        clienteRepo.osservaTutti(),
    ) { tasks, clienti ->
        val byId = clienti.associateBy { it.id }
        val endOfToday = endOfTodayMillis()
        val endOfWeek = endOfToday + 7L * 86400000L
        val righe = tasks.map { t ->
            val grp = when {
                t.origine == OrigineTask.SUGGERITO -> TaskGruppo.SUGGERITI
                t.scadenza <= endOfToday -> TaskGruppo.OGGI
                t.scadenza <= endOfWeek -> TaskGruppo.SETTIMANA
                else -> TaskGruppo.SETTIMANA
            }
            TaskRiga(t, byId[t.clienteId], grp)
        }
        TaskUiState(
            righe = righe,
            totaleAperti = tasks.size,
            totaleUrgenti = tasks.count { it.priorita == com.freela.app.domain.model.Priorita.ALTA },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TaskUiState())

    fun completa(taskId: Long) {
        viewModelScope.launch { taskRepo.completa(taskId) }
    }

    private fun endOfTodayMillis(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }
}
