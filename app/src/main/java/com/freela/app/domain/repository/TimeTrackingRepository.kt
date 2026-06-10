package com.freela.app.domain.repository

import com.freela.app.domain.model.SessioneLavoro
import kotlinx.coroutines.flow.Flow

interface TimeTrackingRepository {
    fun osservaPerCliente(clienteId: Long): Flow<List<SessioneLavoro>>
    fun osservaInCorso(): Flow<SessioneLavoro?>
    fun osservaDurataTotaleMillis(clienteId: Long, now: Long): Flow<Long>
    fun osservaOreTotaliPeriodoMillis(start: Long, end: Long, now: Long): Flow<Long>
    suspend fun avvia(clienteId: Long, descrizione: String?): Long
    suspend fun ferma(sessioneId: Long)
    suspend fun aggiungiManuale(s: SessioneLavoro): Long
    suspend fun elimina(sessioneId: Long)
}
