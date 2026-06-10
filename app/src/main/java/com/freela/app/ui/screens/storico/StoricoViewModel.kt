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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class PeriodoStorico { SETTIMANA, MESE, ANNO }

data class ClienteAttenzione(val cliente: Cliente, val ore: Float)

data class StoricoUiState(
    val periodo: PeriodoStorico = PeriodoStorico.MESE,
    val totOre: Float = 0f,
    val totInterazioni: Int = 0,
    val totIncassato: Double = 0.0,
    val distribuzione: List<ClienteAttenzione> = emptyList(),
    val topClienti: List<Cliente> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StoricoViewModel @Inject constructor(
    private val clienteRepo: ClienteRepository,
    private val timeRepo: TimeTrackingRepository,
    private val interazioneRepo: InterazioneRepository,
    private val finanzeRepo: FinanzeRepository,
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val periodoSel = MutableStateFlow(PeriodoStorico.MESE)

    val state: StateFlow<StoricoUiState> = periodoSel.flatMapLatest { per ->
        val start = now - giorniPeriodo(per) * 86_400_000L
        clienteRepo.osservaTutti().flatMapLatest { clienti ->
            val oreFlows = clienti.map { c ->
                timeRepo.osservaDurataPeriodoMillisCliente(c.id, start, now, now).map { millis ->
                    ClienteAttenzione(c, millis / 3_600_000f)
                }
            }
            val oreCombined = if (oreFlows.isEmpty()) flowOf(emptyList()) else combine(oreFlows) { it.toList() }
            combine(
                oreCombined,
                interazioneRepo.osservaConteggioPeriodo(start, now),
                finanzeRepo.osservaIncassatoPeriodo(start, now),
            ) { distribuzione, interazioni, incassato ->
                StoricoUiState(
                    periodo = per,
                    totOre = distribuzione.sumOf { it.ore.toDouble() }.toFloat(),
                    totInterazioni = interazioni,
                    totIncassato = incassato,
                    distribuzione = distribuzione.sortedByDescending { it.ore },
                    topClienti = distribuzione
                        .filter { it.cliente.importoPreventivato != null }
                        .sortedByDescending { it.cliente.importoPreventivato ?: 0.0 }
                        .take(4)
                        .map { it.cliente },
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StoricoUiState())

    fun selezionaPeriodo(periodo: PeriodoStorico) { periodoSel.value = periodo }

    private fun giorniPeriodo(per: PeriodoStorico): Long = when (per) {
        PeriodoStorico.SETTIMANA -> 7L
        PeriodoStorico.MESE -> 30L
        PeriodoStorico.ANNO -> 365L
    }
}
