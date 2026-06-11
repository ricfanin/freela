package com.freela.app.ui.screens.clienti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.repository.ClienteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class NuovoClienteViewModel @Inject constructor(
    private val clienteRepo: ClienteRepository,
) : ViewModel() {

    fun salva(nome: String, telefono: String, email: String, onDone: () -> Unit) {
        if (nome.isBlank()) return
        viewModelScope.launch {
            clienteRepo.crea(
                Cliente(
                    nome = nome.trim(),
                    telefono = telefono.trim().ifBlank { null },
                    email = email.trim().ifBlank { null },
                ),
            )
            onDone()
        }
    }
}
