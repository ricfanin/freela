package com.freela.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.freela.app.data.local.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tags ORDER BY nome ASC")
    fun osservaTutti(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE nome = :nome LIMIT 1")
    suspend fun byNome(nome: String): TagEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: TagEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tags: List<TagEntity>): List<Long>

    /** Crea il tag se non esiste e ritorna l'id. */
    suspend fun upsert(nome: String): Long {
        val esistente = byNome(nome)
        return esistente?.id ?: insert(TagEntity(nome = nome)).also {
            // se IGNORE → -1 perché collisione su unique nome → rileggi
        }.let { id -> if (id == -1L) byNome(nome)!!.id else id }
    }

    @Query("DELETE FROM tags")
    suspend fun cancellaTutti()
}
