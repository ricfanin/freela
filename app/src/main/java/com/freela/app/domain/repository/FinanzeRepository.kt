package com.freela.app.domain.repository

import com.freela.app.domain.model.Fattura
import com.freela.app.domain.model.Preventivo
import com.freela.app.domain.model.StatoPreventivo
import kotlinx.coroutines.flow.Flow

interface FinanzeRepository {
    fun osservaFatture(): Flow<List<Fattura>>
    fun osservaFatturePerCliente(clienteId: Long): Flow<List<Fattura>>
    fun osservaFattureInRitardo(now: Long): Flow<List<Fattura>>
    fun osservaIncassatoPeriodo(start: Long, end: Long): Flow<Double>
    fun osservaTotaleAttesi(now: Long): Flow<Double>
    fun osservaTotaleRitardo(now: Long): Flow<Double>
    suspend fun creaFattura(f: Fattura): Long
    suspend fun segnaPagata(fatturaId: Long)
    suspend fun eliminaFattura(fatturaId: Long)

    fun osservaPreventivi(): Flow<List<Preventivo>>
    fun osservaPreventiviAperti(): Flow<List<Preventivo>>
    suspend fun creaPreventivo(p: Preventivo): Long
    suspend fun cambiaStatoPreventivo(preventivoId: Long, nuovo: StatoPreventivo)
    suspend fun eliminaPreventivo(preventivoId: Long)
}
