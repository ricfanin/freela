package com.freela.app.domain.usecase

import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.TaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

// prossimo task non completato, mostrato nella riga "prossima azione" del cliente
class DerivaProssimaAzione @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(clienteId: Long): Flow<Task?> =
        taskRepository.osservaProssimoPerCliente(clienteId)
}
