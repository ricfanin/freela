package com.freela.app.ui.screens.pipeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.repository.ClienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class PipelineUiState(
    val clientiPerFase: Map<FasePipeline, List<Cliente>> = emptyMap(),
    val totaleClienti: Int = 0,
    val fasiAttive: Int = 0,
)

@HiltViewModel
class PipelineViewModel @Inject constructor(
    clienteRepo: ClienteRepository,
) : ViewModel() {
    val state: StateFlow<PipelineUiState> = clienteRepo.osservaTutti()
        .map { clienti ->
            val grouped = FasePipeline.ordered.associateWith { fase ->
                clienti.filter { it.faseCorrente == fase }
            }
            PipelineUiState(
                clientiPerFase = grouped,
                totaleClienti = clienti.size,
                fasiAttive = grouped.count { it.value.isNotEmpty() },
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PipelineUiState())
}
