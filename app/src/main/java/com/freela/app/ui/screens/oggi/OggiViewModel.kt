package com.freela.app.ui.screens.oggi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.Fattura
import com.freela.app.domain.model.SessioneLavoro
import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.FinanzeRepository
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TaskRepository
import com.freela.app.domain.repository.TimeTrackingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class PeriodoOggi { SETTIMANA, MESE, ANNO }

data class OggiVoce(
    val titolo: String,
    val sottotitolo: String,
    val clienteId: Long?,
)

// stato della home: sessione in corso, riassunto finanziario e le tre sezioni (da contattare, da consegnare, pagamenti)
data class OggiUiState(
    val nomeUtente: String? = null,
    val sessione: SessioneLavoro? = null,
    val clienteAttivo: Cliente? = null,
    val periodo: PeriodoOggi = PeriodoOggi.MESE,
    val fatturato: Double = 0.0,
    val incassato: Double = 0.0,
    val numClienti: Int = 0,
    val oreMese: Float = 0f,
    val obiettivo: Double = 6000.0,
    val daConsegnare: List<OggiVoce> = emptyList(),
    val pagamenti: List<OggiVoce> = emptyList(),
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OggiViewModel @Inject constructor(
    settings: SettingsRepository,
    clienteRepo: ClienteRepository,
    timeRepo: TimeTrackingRepository,
    finanzeRepo: FinanzeRepository,
    taskRepo: TaskRepository,
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val periodoSel = MutableStateFlow(PeriodoOggi.MESE)

    private data class Acc(
        val nomeUtente: String?,
        val sessione: SessioneLavoro?,
        val clienti: List<Cliente>,
        val incassato: Double,
        val attesi: Double,
        val ritardo: Double = 0.0,
        val oreMillis: Long = 0L,
        val tasks: List<Task> = emptyList(),
        val fattureRitardo: List<Fattura> = emptyList(),
    )

    val state: StateFlow<OggiUiState> = periodoSel.flatMapLatest { per ->
        val (start, end) = rangePeriodo(per)
        combine(
            settings.nomeUtente,
            timeRepo.osservaInCorso(),
            clienteRepo.osservaTutti(),
            finanzeRepo.osservaIncassatoPeriodo(start, end),
            finanzeRepo.osservaTotaleAttesi(now),
        ) { nomeUtente, sessione, clienti, incassato, attesi ->
            Acc(nomeUtente, sessione, clienti, incassato, attesi)
        }
            .combine(finanzeRepo.osservaTotaleRitardo(now)) { acc, ritardo -> acc.copy(ritardo = ritardo) }
            .combine(timeRepo.osservaOreTotaliPeriodoMillis(start, end, now)) { acc, ore -> acc.copy(oreMillis = ore) }
            .combine(taskRepo.osservaAperti()) { acc, tasks -> acc.copy(tasks = tasks) }
            .combine(finanzeRepo.osservaFattureInRitardo(now)) { acc, fatture -> acc.copy(fattureRitardo = fatture) }
            .map { acc -> toUiState(acc, per) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), OggiUiState())

    fun selezionaPeriodo(periodo: PeriodoOggi) { periodoSel.value = periodo }

    private fun toUiState(acc: Acc, per: PeriodoOggi): OggiUiState {
        val byId = acc.clienti.associateBy { it.id }
        val nome = { id: Long? -> id?.let { byId[it]?.nome } ?: "—" }
        val endOfToday = endOfTodayMillis()

        val daConsegnare = acc.tasks
            .filter { it.scadenza <= endOfToday }
            .sortedBy { it.scadenza }
            .map { OggiVoce(it.titolo, nome(it.clienteId), it.clienteId) }

        val pagamenti = acc.fattureRitardo.map { f ->
            val giorni = ((now - f.dataScadenza) / 86_400_000L).coerceAtLeast(0)
            OggiVoce("#${f.numero} · ${nome(f.clienteId)}", "scaduta da ${giorni}g", f.clienteId)
        }

        return OggiUiState(
            nomeUtente = acc.nomeUtente,
            sessione = acc.sessione,
            clienteAttivo = acc.sessione?.let { s -> acc.clienti.firstOrNull { it.id == s.clienteId } },
            periodo = per,
            fatturato = acc.incassato + acc.attesi + acc.ritardo,
            incassato = acc.incassato,
            numClienti = acc.clienti.size,
            oreMese = acc.oreMillis / 3_600_000f,
            obiettivo = 6000.0,
            daConsegnare = daConsegnare,
            pagamenti = pagamenti,
            isLoading = false,
        )
    }

    private fun rangePeriodo(per: PeriodoOggi): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        return when (per) {
            PeriodoOggi.SETTIMANA -> {
                val end = endOfTodayMillis()
                (end - 6 * 86_400_000L - 86_399_999L) to end
            }
            PeriodoOggi.MESE -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val start = cal.timeInMillis
                val end = (cal.clone() as Calendar).apply { add(Calendar.MONTH, 1) }.timeInMillis - 1
                start to end
            }
            PeriodoOggi.ANNO -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                val start = cal.timeInMillis
                val end = (cal.clone() as Calendar).apply { add(Calendar.YEAR, 1) }.timeInMillis - 1
                start to end
            }
        }
    }

    private fun endOfTodayMillis(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59); set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
    }.timeInMillis
}
