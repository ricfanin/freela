package com.freela.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.freela.app.data.local.entity.SessioneLavoroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessioneLavoroDao {

    @Query("SELECT * FROM sessioni_lavoro ORDER BY inizio DESC")
    fun osservaTutte(): Flow<List<SessioneLavoroEntity>>

    @Query("SELECT * FROM sessioni_lavoro WHERE clienteId = :clienteId ORDER BY inizio DESC")
    fun osservaPerCliente(clienteId: Long): Flow<List<SessioneLavoroEntity>>

    @Query("""
        SELECT IFNULL(SUM(IFNULL(fine, :now) - inizio), 0)
        FROM sessioni_lavoro WHERE clienteId = :clienteId
    """)
    fun osservaDurataTotaleMillisCliente(clienteId: Long, now: Long): Flow<Long>

    @Query("SELECT * FROM sessioni_lavoro WHERE fine IS NULL LIMIT 1")
    fun osservaSessioneInCorso(): Flow<SessioneLavoroEntity?>

    @Query("""
        SELECT IFNULL(SUM(IFNULL(fine, :now) - inizio), 0)
        FROM sessioni_lavoro
        WHERE inizio BETWEEN :start AND :end
    """)
    fun osservaDurataPeriodoMillis(start: Long, end: Long, now: Long): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(s: SessioneLavoroEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SessioneLavoroEntity>)

    @Query("UPDATE sessioni_lavoro SET fine = :ora WHERE id = :id")
    suspend fun chiudi(id: Long, ora: Long)

    @Query("DELETE FROM sessioni_lavoro WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM sessioni_lavoro")
    suspend fun cancellaTutte()
}
