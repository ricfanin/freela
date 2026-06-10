package com.freela.app.data.repository

import com.freela.app.data.local.dao.SessioneLavoroDao
import com.freela.app.data.local.entity.SessioneLavoroEntity
import com.freela.app.data.mapper.toDomain
import com.freela.app.data.mapper.toEntity
import com.freela.app.domain.model.SessioneLavoro
import com.freela.app.domain.repository.TimeTrackingRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTimeTrackingRepository @Inject constructor(
    private val dao: SessioneLavoroDao,
) : TimeTrackingRepository {

    override fun osservaPerCliente(clienteId: Long): Flow<List<SessioneLavoro>> =
        dao.osservaPerCliente(clienteId).map { list -> list.map { it.toDomain() } }

    override fun osservaInCorso(): Flow<SessioneLavoro?> = dao.osservaSessioneInCorso().map { it?.toDomain() }

    override fun osservaDurataTotaleMillis(clienteId: Long, now: Long): Flow<Long> =
        dao.osservaDurataTotaleMillisCliente(clienteId, now)

    override fun osservaOreTotaliPeriodoMillis(start: Long, end: Long, now: Long): Flow<Long> =
        dao.osservaDurataPeriodoMillis(start, end, now)

    override suspend fun avvia(clienteId: Long, descrizione: String?): Long =
        dao.insert(SessioneLavoroEntity(clienteId = clienteId, inizio = System.currentTimeMillis(), descrizione = descrizione))

    override suspend fun ferma(sessioneId: Long) = dao.chiudi(sessioneId, System.currentTimeMillis())

    override suspend fun aggiungiManuale(s: SessioneLavoro): Long = dao.insert(s.copy(inserimentoManuale = true).toEntity())

    override suspend fun elimina(sessioneId: Long) = dao.delete(sessioneId)
}
