package com.freela.app.domain.repository

import com.freela.app.domain.model.Interazione
import kotlinx.coroutines.flow.Flow

interface InterazioneRepository {
    fun osservaPerCliente(clienteId: Long): Flow<List<Interazione>>
    fun osservaUltimaData(clienteId: Long): Flow<Long?>
    fun osservaConteggioPeriodo(start: Long, end: Long): Flow<Int>
    suspend fun aggiungi(interazione: Interazione): Long
    suspend fun elimina(id: Long)
}
