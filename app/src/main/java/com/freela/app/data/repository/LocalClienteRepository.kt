package com.freela.app.data.repository

import androidx.room.withTransaction
import com.freela.app.data.local.FreelaDatabase
import com.freela.app.data.local.dao.ClienteDao
import com.freela.app.data.local.dao.TagDao
import com.freela.app.data.local.entity.ClienteTagCrossRef
import com.freela.app.data.mapper.toDomain
import com.freela.app.data.mapper.toEntity
import com.freela.app.domain.model.Cliente
import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.model.Tag
import com.freela.app.domain.repository.ClienteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalClienteRepository @Inject constructor(
    private val db: FreelaDatabase,
    private val clienteDao: ClienteDao,
    private val tagDao: TagDao,
) : ClienteRepository {

    override fun osservaTutti(): Flow<List<Cliente>> =
        clienteDao.osservaTuttiConTags().map { list -> list.map { it.toDomain() } }

    override fun osserva(id: Long): Flow<Cliente?> =
        clienteDao.osservaConTags(id).map { it?.toDomain() }

    override fun cerca(query: String): Flow<List<Cliente>> =
        clienteDao.cerca(query).map { list -> list.map { it.toDomain() } }

    override fun osservaTags(): Flow<List<Tag>> =
        tagDao.osservaTutti().map { list -> list.map { it.toDomain() } }

    override suspend fun crea(cliente: Cliente, tags: List<String>): Long = db.withTransaction {
        val id = clienteDao.insert(cliente.toEntity())
        val tagIds = tags.map { tagDao.upsert(it) }
        clienteDao.insertTagCrossRefs(tagIds.map { tagId -> ClienteTagCrossRef(id, tagId) })
        id
    }

    override suspend fun aggiorna(cliente: Cliente) {
        clienteDao.update(cliente.toEntity())
    }

    override suspend fun cambiaFase(clienteId: Long, fase: FasePipeline) {
        clienteDao.aggiornaFase(clienteId, fase)
    }

    override suspend fun elimina(clienteId: Long) {
        clienteDao.delete(clienteId)
    }
}
