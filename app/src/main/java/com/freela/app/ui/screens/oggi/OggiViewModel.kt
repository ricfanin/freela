package com.freela.app.ui.screens.oggi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.Fattura
import com.freela.app.domain.model.PersonaDemo
import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.FinanzeRepository
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TaskRepository
import com.freela.app.domain.repository.TimeTrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class OggiUiState(
    val persona: PersonaDemo? = null,
    val totRitardo: Double = 0.0,
    val totAttesi: Double = 0.0,
    val totOreSettimana: Float = 0f,
    val daContattare: List<TaskConCliente> = emptyList(),
    val daConsegnare: List<TaskConCliente> = emptyList(),
    val pagamenti: List<FatturaConCliente> = emptyList(),
    val suggerimento: SuggerimentoUi? = null,
    val isLoading: Boolean = true,
)

data class TaskConCliente(val task: Task, val cliente: Cliente?)
data class FatturaConCliente(val fattura: Fattura, val cliente: Cliente?, val giorniRitardo: Int)
data class SuggerimentoUi(val testo: String, val clienteNome: String?)

@HiltViewModel
class OggiViewModel @Inject constructor(
    settings: SettingsRepository,
    clienteRepo: ClienteRepository,
    taskRepo: TaskRepository,
    private val timeRepo: TimeTrackingRepository,
    finanzeRepo: FinanzeRepository,
) : ViewModel() {

    private val now = System.currentTimeMillis()

    val state: StateFlow<OggiUiState> = combine(
        settings.personaCorrente,
        clienteRepo.osservaTutti(),
        taskRepo.osservaAperti(),
        finanzeRepo.osservaFattureNonPagate(),
        finanzeRepo.osservaTotaleAttesi(now),
    ) { persona, clienti, tasks, fatture, totAttesi ->
        val clientiById = clienti.associateBy { it.id }

        val (inRitardo, _) = fatture.partition { it.dataScadenza < now }
        val totRitardo = inRitardo.sumOf { it.importo }

        val endOfDay = endOfTodayMillis()
        val daContattare = tasks
            .filter { it.scadenza <= endOfDay }
            .filter { it.clienteId != null }
            .take(4)
            .map { TaskConCliente(it, clientiById[it.clienteId]) }
        val daConsegnare = tasks
            .filter { it.scadenza > endOfDay && it.scadenza <= endOfDay + 7L * 86400000L }
            .take(4)
            .map { TaskConCliente(it, clientiById[it.clienteId]) }
        val pagamenti = fatture.take(4).map { f ->
            val giorniRitardo = if (f.dataScadenza < now)
                ((now - f.dataScadenza) / 86400000L).toInt() else 0
            FatturaConCliente(f, clientiById[f.clienteId], giorniRitardo)
        }

        val suggerimento = tasks.firstOrNull { it.origine == com.freela.app.domain.model.OrigineTask.SUGGERITO }
            ?.let { sug ->
                val nome = clientiById[sug.clienteId]?.nome
                SuggerimentoUi(testo = sug.titolo, clienteNome = nome)
            }

        OggiUiState(
            persona = persona,
            totRitardo = totRitardo,
            totAttesi = totAttesi,
            totOreSettimana = computeOreSettimanaPlaceholder(),
            daContattare = daContattare,
            daConsegnare = daConsegnare,
            pagamenti = pagamenti,
            suggerimento = suggerimento,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OggiUiState())

    private fun endOfTodayMillis(): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    // V1: somma fissa da seed (PRD §11.4 fase 8 fa lo Sessioni reali).
    private fun computeOreSettimanaPlaceholder(): Float = 14.5f
}
