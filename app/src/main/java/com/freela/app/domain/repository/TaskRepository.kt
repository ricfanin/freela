package com.freela.app.domain.repository

import com.freela.app.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun osservaTutti(): Flow<List<Task>>
    fun osservaAperti(): Flow<List<Task>>
    fun osservaInIntervallo(startMillis: Long, endMillis: Long): Flow<List<Task>>
    fun osservaProssimoPerCliente(clienteId: Long): Flow<Task?>
    suspend fun crea(task: Task): Long
    suspend fun aggiorna(task: Task)
    suspend fun completa(taskId: Long)
    suspend fun elimina(taskId: Long)
}
