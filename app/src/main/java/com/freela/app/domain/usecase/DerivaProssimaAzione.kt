package com.freela.app.domain.usecase

import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.TaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Per la riga "Prossima azione" sulla scheda cliente: prossimo task non completato.
 */
class DerivaProssimaAzione @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(clienteId: Long): Flow<Task?> =
        taskRepository.osservaProssimoPerCliente(clienteId)
}
