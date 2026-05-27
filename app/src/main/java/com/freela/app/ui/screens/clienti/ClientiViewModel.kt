package com.freela.app.ui.screens.clienti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.repository.ClienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ClientiUiState(
    val query: String = "",
    val sezioni: Map<Char, List<Cliente>> = emptyMap(),
    val totaleAttivi: Int = 0,
    val totaleRicorrenti: Int = 0,
)

@OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class ClientiViewModel @Inject constructor(
    private val clienteRepo: ClienteRepository,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val state: StateFlow<ClientiUiState> = _query
        .debounce(80)
        .flatMapLatest { q -> if (q.isBlank()) clienteRepo.osservaTutti() else clienteRepo.cerca(q) }
        .combine(_query) { clienti, q ->
            val sorted = clienti.sortedBy { it.nome.lowercase() }
            val sezioni = sorted.groupBy { it.nome.firstOrNull()?.uppercaseChar() ?: '#' }
            ClientiUiState(
                query = q,
                sezioni = sezioni,
                totaleAttivi = sorted.size,
                totaleRicorrenti = sorted.count { it.faseCorrente == FasePipeline.CLIENTE_RICORRENTE },
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClientiUiState())

    fun aggiornaQuery(q: String) { _query.value = q }
}
