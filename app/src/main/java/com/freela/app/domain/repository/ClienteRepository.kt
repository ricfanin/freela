package com.freela.app.domain.repository

import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.FasePipeline
import kotlinx.coroutines.flow.Flow

interface ClienteRepository {
    fun osservaTutti(): Flow<List<Cliente>>
    fun osserva(id: Long): Flow<Cliente?>
    fun cerca(query: String): Flow<List<Cliente>>
    suspend fun crea(cliente: Cliente, tags: List<String> = emptyList()): Long
    suspend fun aggiorna(cliente: Cliente, tags: List<String>? = null)
    suspend fun cambiaFase(clienteId: Long, fase: FasePipeline)
    suspend fun cambiaPreferito(clienteId: Long, preferito: Boolean)
    suspend fun elimina(clienteId: Long)
}
