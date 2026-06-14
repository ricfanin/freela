package com.freela.app.data.repository

import com.freela.app.data.local.dao.FatturaDao
import com.freela.app.data.local.dao.PreventivoDao
import com.freela.app.data.mapper.toDomain
import com.freela.app.data.mapper.toEntity
import com.freela.app.domain.model.Fattura
import com.freela.app.domain.model.Preventivo
import com.freela.app.domain.model.StatoPreventivo
import com.freela.app.domain.repository.FinanzeRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalFinanzeRepository @Inject constructor(
    private val fatturaDao: FatturaDao,
    private val preventivoDao: PreventivoDao,
) : FinanzeRepository {

    override fun osservaFatture(): Flow<List<Fattura>> =
        fatturaDao.osservaTutte().map { list -> list.map { it.toDomain() } }

    override fun osservaFatturePerCliente(clienteId: Long): Flow<List<Fattura>> =
        fatturaDao.osservaPerCliente(clienteId).map { list -> list.map { it.toDomain() } }

    override fun osservaFattureInRitardo(now: Long): Flow<List<Fattura>> =
        fatturaDao.osservaInRitardo(now).map { list -> list.map { it.toDomain() } }

    override fun osservaIncassatoPeriodo(start: Long, end: Long): Flow<Double> =
        fatturaDao.osservaIncassatoNelPeriodo(start, end)

    override fun osservaTotaleAttesi(now: Long): Flow<Double> = fatturaDao.osservaTotaleAttesi(now)

    override fun osservaTotaleRitardo(now: Long): Flow<Double> = fatturaDao.osservaTotaleInRitardo(now)

    override suspend fun creaFattura(f: Fattura): Long = fatturaDao.insert(f.toEntity())

    override suspend fun segnaPagata(fatturaId: Long) =
        fatturaDao.segnaPagata(fatturaId, System.currentTimeMillis())

    override suspend fun eliminaFattura(fatturaId: Long) = fatturaDao.delete(fatturaId)

    override fun osservaPreventivi(): Flow<List<Preventivo>> =
        preventivoDao.osservaTutti().map { list -> list.map { it.toDomain() } }

    override fun osservaPreventiviAperti(): Flow<List<Preventivo>> = preventivoDao
        .osservaPerStati(listOf(StatoPreventivo.INVIATO, StatoPreventivo.ACCETTATO))
        .map { list -> list.map { it.toDomain() } }

    override suspend fun creaPreventivo(p: Preventivo): Long = preventivoDao.insert(p.toEntity())

    override suspend fun cambiaStatoPreventivo(preventivoId: Long, nuovo: StatoPreventivo) =
        preventivoDao.aggiornaStato(preventivoId, nuovo)

    override suspend fun eliminaPreventivo(preventivoId: Long) = preventivoDao.delete(preventivoId)
}
