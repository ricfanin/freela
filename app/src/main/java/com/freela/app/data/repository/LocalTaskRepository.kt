package com.freela.app.data.repository

import com.freela.app.data.local.dao.TaskDao
import com.freela.app.data.mapper.toDomain
import com.freela.app.data.mapper.toEntity
import com.freela.app.domain.model.Task
import com.freela.app.domain.repository.TaskRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTaskRepository @Inject constructor(
    private val dao: TaskDao,
) : TaskRepository {

    override fun osservaTutti(): Flow<List<Task>> = dao.osservaTutti().map { list -> list.map { it.toDomain() } }

    override fun osservaAperti(): Flow<List<Task>> = dao.osservaAperti().map { list -> list.map { it.toDomain() } }

    override fun osservaInIntervallo(startMillis: Long, endMillis: Long): Flow<List<Task>> =
        dao.osservaInIntervallo(startMillis, endMillis).map { list -> list.map { it.toDomain() } }

    override fun osservaProssimoPerCliente(clienteId: Long): Flow<Task?> =
        dao.osservaProssimoPerCliente(clienteId).map { it?.toDomain() }

    override suspend fun crea(task: Task): Long = dao.insert(task.toEntity())

    override suspend fun aggiorna(task: Task) = dao.update(task.toEntity())

    override suspend fun completa(taskId: Long) = dao.completa(taskId, System.currentTimeMillis())

    override suspend fun elimina(taskId: Long) = dao.delete(taskId)
}
