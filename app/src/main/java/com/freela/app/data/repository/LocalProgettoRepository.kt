package com.freela.app.data.repository

import com.freela.app.data.local.dao.ProgettoDao
import com.freela.app.data.mapper.toDomain
import com.freela.app.data.mapper.toEntity
import com.freela.app.domain.model.Progetto
import com.freela.app.domain.repository.ProgettoRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalProgettoRepository @Inject constructor(
    private val dao: ProgettoDao,
) : ProgettoRepository {

    override fun osservaTutti(): Flow<List<Progetto>> =
        dao.osservaTutti().map { list -> list.map { it.toDomain() } }

    override fun osserva(id: Long): Flow<Progetto?> =
        dao.osserva(id).map { it?.toDomain() }

    override suspend fun crea(progetto: Progetto): Long = dao.insert(progetto.toEntity())

    override suspend fun aggiorna(progetto: Progetto) = dao.update(progetto.toEntity())

    override suspend fun elimina(id: Long) = dao.delete(id)
}
