package com.freela.app.ui.screens.progetti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.Progetto
import com.freela.app.domain.model.StatoProgetto
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.ProgettoRepository
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
) : ViewModel() {

    val clienti: StateFlow<List<Cliente>> = clienteRepo.osservaTutti()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun salva(clienteId: Long, nome: String, oreStimate: Int, onDone: () -> Unit) {
        if (clienteId == 0L || nome.isBlank()) return
        viewModelScope.launch {
            progettoRepo.crea(
                Progetto(
                    clienteId = clienteId,
                    nome = nome.trim(),
                    oreStimate = oreStimate,
                    stato = StatoProgetto.DA_INIZIARE,
                ),
            )
            onDone()
        }
    }
}
