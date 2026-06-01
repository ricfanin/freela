package com.freela.app.ui.screens.finanze

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.Fattura
import com.freela.app.domain.model.Preventivo
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.FinanzeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class FatturaRiga(val fattura: Fattura, val cliente: Cliente?, val statoUi: com.freela.app.domain.model.StatoFatturaUi)
data class PreventivoRiga(val preventivo: Preventivo, val cliente: Cliente?)

data class FinanzeUiState(
    val fatturatoMese: Double = 0.0,
    val incassato: Double = 0.0,
    val attesi: Double = 0.0,
    val inRitardo: Double = 0.0,
    val fatture: List<FatturaRiga> = emptyList(),
    val preventivi: List<PreventivoRiga> = emptyList(),
)

@HiltViewModel
class FinanzeViewModel @Inject constructor(
    finanzeRepo: FinanzeRepository,
    clienteRepo: ClienteRepository,
) : ViewModel() {

    private val now = System.currentTimeMillis()
    private val startMese = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1); set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
    }.timeInMillis
    private val endMese = Calendar.getInstance().apply {
        timeInMillis = startMese; add(Calendar.MONTH, 1)
    }.timeInMillis - 1

    val state: StateFlow<FinanzeUiState> = combine(
        finanzeRepo.osservaFatture(),
        finanzeRepo.osservaPreventiviAperti(),
        clienteRepo.osservaTutti(),
        finanzeRepo.osservaIncassatoPeriodo(startMese, endMese),
    ) { fatture, preventivi, clienti, incassatoMese ->
        val byId = clienti.associateBy { it.id }
        val fattureRighe = fatture.map { f ->
            FatturaRiga(f, byId[f.clienteId], f.statoUi(now))
        }
        val preventiviRighe = preventivi.map { p -> PreventivoRiga(p, byId[p.clienteId]) }
        val attesi = fatture.filter { it.statoUi(now) == com.freela.app.domain.model.StatoFatturaUi.EMESSA }.sumOf { it.importo }
        val ritardo = fatture.filter { it.statoUi(now) == com.freela.app.domain.model.StatoFatturaUi.IN_RITARDO }.sumOf { it.importo }
        val fatturato = incassatoMese + attesi + ritardo
        FinanzeUiState(
            fatturatoMese = fatturato,
            incassato = incassatoMese,
            attesi = attesi,
            inRitardo = ritardo,
            fatture = fattureRighe,
            preventivi = preventiviRighe,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FinanzeUiState())
}
