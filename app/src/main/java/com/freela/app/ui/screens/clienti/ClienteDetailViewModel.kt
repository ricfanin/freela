package com.freela.app.ui.screens.clienti

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.data.location.LocationProvider
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.model.Fattura
import com.freela.app.domain.model.Interazione
import com.freela.app.domain.model.Task
import com.freela.app.domain.model.TipoInterazione
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
import kotlinx.coroutines.launch

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
    private val clienteRepo: ClienteRepository,
    private val interazioneRepo: InterazioneRepository,
    private val locationProvider: LocationProvider,
    private val taskRepo: TaskRepository,
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

    fun cambiaFase(fase: FasePipeline) {
        if (clienteId == 0L) return
        viewModelScope.launch { clienteRepo.cambiaFase(clienteId, fase) }
    }

    fun cambiaPreferito() {
        val corrente = state.value.cliente ?: return
        viewModelScope.launch { clienteRepo.cambiaPreferito(clienteId, !corrente.preferito) }
    }

    fun aggiornaCliente(nome: String, telefono: String?, email: String?, fonte: String?, note: String?) {
        val corrente = state.value.cliente ?: return
        viewModelScope.launch {
            clienteRepo.aggiorna(
                corrente.copy(
                    nome = nome.trim(),
                    telefono = telefono?.trim()?.ifBlank { null },
                    email = email?.trim()?.ifBlank { null },
                    fonteAcquisizione = fonte?.trim()?.ifBlank { null },
                    note = note?.trim()?.ifBlank { null },
                ),
            )
        }
    }

    fun elimina(onDone: () -> Unit) {
        if (clienteId == 0L) return
        viewModelScope.launch {
            clienteRepo.elimina(clienteId)
            onDone()
        }
    }

    fun aggiungiReminder(titolo: String, giorni: Int) {
        if (clienteId == 0L || titolo.isBlank()) return
        val scadenza = System.currentTimeMillis() + giorni.coerceAtLeast(0) * 86_400_000L
        viewModelScope.launch {
            taskRepo.crea(
                Task(
                    titolo = titolo.trim(),
                    clienteId = clienteId,
                    scadenza = scadenza,
                ),
            )
        }
    }

    fun aggiungiInterazione(
        tipo: TipoInterazione,
        descrizione: String?,
        durataMinuti: Int?,
        conPosizione: Boolean,
    ) {
        if (clienteId == 0L) return
        viewModelScope.launch {
            var lat: Double? = null
            var lon: Double? = null
            var indirizzo: String? = null
            if (conPosizione && tipo == TipoInterazione.MEETING) {
                locationProvider.posizioneCorrente()?.let { coord ->
                    lat = coord.latitudine
                    lon = coord.longitudine
                    indirizzo = locationProvider.indirizzoDa(coord.latitudine, coord.longitudine)
                }
            }
            interazioneRepo.aggiungi(
                Interazione(
                    clienteId = clienteId,
                    tipo = tipo,
                    data = System.currentTimeMillis(),
                    durataMinuti = durataMinuti,
                    descrizione = descrizione,
                    latitudine = lat,
                    longitudine = lon,
                    indirizzo = indirizzo,
                ),
            )
        }
    }
}
