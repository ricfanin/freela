package com.freela.app.domain.repository

import com.freela.app.domain.model.Progetto
import kotlinx.coroutines.flow.Flow

interface ProgettoRepository {
    fun osservaTutti(): Flow<List<Progetto>>
    fun osserva(id: Long): Flow<Progetto?>
    suspend fun crea(progetto: Progetto): Long
    suspend fun aggiorna(progetto: Progetto)
    suspend fun elimina(id: Long)
}
