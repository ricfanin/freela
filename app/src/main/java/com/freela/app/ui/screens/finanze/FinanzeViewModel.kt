package com.freela.app.ui.screens.finanze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.Fattura
import com.freela.app.domain.model.Preventivo
import com.freela.app.domain.model.StatoFatturaUi
import com.freela.app.domain.model.StatoPreventivo
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.FinanzeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FatturaRiga(val fattura: Fattura, val cliente: Cliente?, val statoUi: StatoFatturaUi)
data class PreventivoRiga(val preventivo: Preventivo, val cliente: Cliente?)

data class FinanzeUiState(
    val meseLabel: String = "",
    val meseOffset: Int = 0,
    val fatturatoMese: Double = 0.0,
    val incassato: Double = 0.0,
    val attesi: Double = 0.0,
    val inRitardo: Double = 0.0,
    val fatture: List<FatturaRiga> = emptyList(),
    val preventivi: List<PreventivoRiga> = emptyList(),
    val clienti: List<Cliente> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FinanzeViewModel @Inject constructor(
    private val finanzeRepo: FinanzeRepository,
    private val clienteRepo: ClienteRepository,
) : ViewModel() {

    private val now = System.currentTimeMillis()

    /** Offset rispetto al mese corrente (0 = questo mese, -1 = mese scorso, …). */
    private val meseOffset = MutableStateFlow(0)

    val state: StateFlow<FinanzeUiState> = meseOffset.flatMapLatest { offset ->
        val cal = Calendar.getInstance().apply {
            add(Calendar.MONTH, offset)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val startMese = cal.timeInMillis
        val endMese = (cal.clone() as Calendar).apply { add(Calendar.MONTH, 1) }.timeInMillis - 1
        val label = SimpleDateFormat("MMMM yyyy", Locale.ITALIAN)
            .format(Date(startMese)).replaceFirstChar { it.uppercase() }
        combine(
            finanzeRepo.osservaFatture(),
            finanzeRepo.osservaPreventiviAperti(),
            clienteRepo.osservaTutti(),
            finanzeRepo.osservaIncassatoPeriodo(startMese, endMese),
        ) { fatture, preventivi, clienti, incassatoMese ->
            val byId = clienti.associateBy { it.id }
            val fattureRighe = fatture.map { f -> FatturaRiga(f, byId[f.clienteId], f.statoUi(now)) }
            val preventiviRighe = preventivi.map { p -> PreventivoRiga(p, byId[p.clienteId]) }
            val attesi = fatture.filter { it.statoUi(now) == StatoFatturaUi.EMESSA }.sumOf { it.importo }
            val ritardo = fatture.filter { it.statoUi(now) == StatoFatturaUi.IN_RITARDO }.sumOf { it.importo }
            FinanzeUiState(
                meseLabel = label,
                meseOffset = offset,
                fatturatoMese = incassatoMese + attesi + ritardo,
                incassato = incassatoMese,
                attesi = attesi,
                inRitardo = ritardo,
                fatture = fattureRighe,
                preventivi = preventiviRighe,
                clienti = clienti,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FinanzeUiState())

    fun selezionaMese(offset: Int) { meseOffset.value = offset }

    fun creaFattura(numero: String, clienteId: Long, importo: Double, dataScadenza: Long) {
        viewModelScope.launch {
            finanzeRepo.creaFattura(
                Fattura(
                    numero = numero,
                    clienteId = clienteId,
                    importo = importo,
                    dataEmissione = System.currentTimeMillis(),
                    dataScadenza = dataScadenza,
                ),
            )
        }
    }

    fun creaPreventivo(clienteId: Long, importo: Double, note: String?) {
        viewModelScope.launch {
            finanzeRepo.creaPreventivo(
                Preventivo(
                    clienteId = clienteId,
                    importo = importo,
                    dataInvio = System.currentTimeMillis(),
                    note = note,
                ),
            )
        }
    }

    fun segnaPagata(fatturaId: Long) { viewModelScope.launch { finanzeRepo.segnaPagata(fatturaId) } }

    fun eliminaFattura(fatturaId: Long) { viewModelScope.launch { finanzeRepo.eliminaFattura(fatturaId) } }

    fun cambiaStatoPreventivo(preventivoId: Long, stato: StatoPreventivo) {
        viewModelScope.launch { finanzeRepo.cambiaStatoPreventivo(preventivoId, stato) }
    }

    fun eliminaPreventivo(preventivoId: Long) { viewModelScope.launch { finanzeRepo.eliminaPreventivo(preventivoId) } }
}
