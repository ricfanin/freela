package com.freela.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.freela.app.data.local.entity.PreventivoEntity
import com.freela.app.domain.model.StatoPreventivo
import kotlinx.coroutines.flow.Flow

@Dao
interface PreventivoDao {

    @Query("SELECT * FROM preventivi ORDER BY dataInvio DESC")
    fun osservaTutti(): Flow<List<PreventivoEntity>>

    @Query("SELECT * FROM preventivi WHERE stato IN (:stati) ORDER BY dataInvio DESC")
    fun osservaPerStati(stati: List<StatoPreventivo>): Flow<List<PreventivoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(p: PreventivoEntity): Long

    @Query("UPDATE preventivi SET stato = :stato WHERE id = :id")
    suspend fun aggiornaStato(id: Long, stato: StatoPreventivo)

    @Query("DELETE FROM preventivi WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM preventivi")
    suspend fun cancellaTutti()
}
