package com.freela.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.freela.app.data.local.entity.InterazioneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterazioneDao {

    @Query("SELECT * FROM interazioni ORDER BY data DESC")
    fun osservaTutte(): Flow<List<InterazioneEntity>>

    @Query("SELECT * FROM interazioni WHERE clienteId = :clienteId ORDER BY data DESC")
    fun osservaPerCliente(clienteId: Long): Flow<List<InterazioneEntity>>

    @Query("SELECT MAX(data) FROM interazioni WHERE clienteId = :clienteId")
    fun osservaUltimaData(clienteId: Long): Flow<Long?>

    @Query("SELECT COUNT(*) FROM interazioni WHERE data BETWEEN :start AND :end")
    fun osservaConteggioPeriodo(start: Long, end: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(interazione: InterazioneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InterazioneEntity>)

    @Query("DELETE FROM interazioni WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM interazioni")
    suspend fun cancellaTutte()
}
