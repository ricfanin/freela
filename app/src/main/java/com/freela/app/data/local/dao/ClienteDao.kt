package com.freela.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.freela.app.data.local.entity.ClienteEntity
import com.freela.app.data.local.entity.ClienteTagCrossRef
import com.freela.app.data.local.entity.ClienteWithTags
import com.freela.app.domain.model.FasePipeline
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {

    @Query("SELECT * FROM clienti ORDER BY nome ASC")
    fun osservaTutti(): Flow<List<ClienteEntity>>

    @Transaction
    @Query("SELECT * FROM clienti ORDER BY nome ASC")
    fun osservaTuttiConTags(): Flow<List<ClienteWithTags>>

    @Transaction
    @Query("SELECT * FROM clienti WHERE id = :id LIMIT 1")
    fun osservaConTags(id: Long): Flow<ClienteWithTags?>

    @Query("SELECT * FROM clienti WHERE id = :id LIMIT 1")
    suspend fun byId(id: Long): ClienteEntity?

    @Transaction
    @Query("""
        SELECT * FROM clienti
        WHERE (:q IS NULL OR :q = ''
               OR LOWER(nome) LIKE '%' || LOWER(:q) || '%'
               OR LOWER(IFNULL(telefono,'')) LIKE '%' || LOWER(:q) || '%'
               OR id IN (
                   SELECT ctr.clienteId FROM cliente_tag ctr
                   JOIN tags t ON t.id = ctr.tagId
                   WHERE LOWER(t.nome) LIKE '%' || LOWER(:q) || '%'
               ))
        ORDER BY nome ASC
    """)
    fun cercaConTags(q: String?): Flow<List<ClienteWithTags>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cliente: ClienteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clienti: List<ClienteEntity>): List<Long>

    @Update
    suspend fun update(cliente: ClienteEntity)

    @Query("UPDATE clienti SET faseCorrente = :fase WHERE id = :id")
    suspend fun aggiornaFase(id: Long, fase: FasePipeline)

    @Query("UPDATE clienti SET preferito = :preferito WHERE id = :id")
    suspend fun aggiornaPreferito(id: Long, preferito: Boolean)

    @Query("DELETE FROM clienti WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM clienti")
    suspend fun cancellaTutti()

    // ---- Cross-ref tag ----
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTagCrossRef(ref: ClienteTagCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTagCrossRefs(refs: List<ClienteTagCrossRef>)

    @Query("DELETE FROM cliente_tag WHERE clienteId = :clienteId")
    suspend fun cancellaTagsCliente(clienteId: Long)
}
