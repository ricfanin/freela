package com.freela.app.data.repository

import com.freela.app.data.local.dao.InterazioneDao
import com.freela.app.data.mapper.toDomain
import com.freela.app.data.mapper.toEntity
import com.freela.app.domain.model.Interazione
import com.freela.app.domain.repository.InterazioneRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalInterazioneRepository @Inject constructor(
    private val dao: InterazioneDao,
) : InterazioneRepository {

    override fun osservaPerCliente(clienteId: Long): Flow<List<Interazione>> =
        dao.osservaPerCliente(clienteId).map { list -> list.map { it.toDomain() } }

    override fun osservaConteggioPeriodo(start: Long, end: Long): Flow<Int> =
        dao.osservaConteggioPeriodo(start, end)

    override suspend fun aggiungi(interazione: Interazione): Long = dao.insert(interazione.toEntity())

    override suspend fun elimina(id: Long) = dao.delete(id)
}
