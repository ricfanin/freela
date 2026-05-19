package com.freela.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.freela.app.data.local.entity.FatturaEntity
import com.freela.app.domain.model.StatoFattura
import kotlinx.coroutines.flow.Flow

@Dao
interface FatturaDao {

    @Query("SELECT * FROM fatture ORDER BY dataScadenza ASC")
    fun osservaTutte(): Flow<List<FatturaEntity>>

    @Query("SELECT * FROM fatture WHERE clienteId = :clienteId ORDER BY dataScadenza ASC")
    fun osservaPerCliente(clienteId: Long): Flow<List<FatturaEntity>>

    @Query("SELECT * FROM fatture WHERE stato = :stato ORDER BY dataScadenza ASC")
    fun osservaPerStato(stato: StatoFattura): Flow<List<FatturaEntity>>

    @Query("""
        SELECT * FROM fatture
        WHERE stato = 'EMESSA' AND dataScadenza < :now
        ORDER BY dataScadenza ASC
    """)
    fun osservaInRitardo(now: Long): Flow<List<FatturaEntity>>

    @Query("""
        SELECT * FROM fatture
        WHERE stato = 'EMESSA'
        ORDER BY dataScadenza ASC
    """)
    fun osservaNonPagate(): Flow<List<FatturaEntity>>

    @Query("""
        SELECT IFNULL(SUM(importo), 0) FROM fatture
        WHERE stato = 'PAGATA' AND dataPagamento BETWEEN :startMillis AND :endMillis
    """)
    fun osservaIncassatoNelPeriodo(startMillis: Long, endMillis: Long): Flow<Double>

    @Query("""
        SELECT IFNULL(SUM(importo), 0) FROM fatture
        WHERE stato = 'EMESSA' AND dataScadenza < :now
    """)
    fun osservaTotaleInRitardo(now: Long): Flow<Double>

    @Query("""
        SELECT IFNULL(SUM(importo), 0) FROM fatture
        WHERE stato = 'EMESSA' AND dataScadenza >= :now
    """)
    fun osservaTotaleAttesi(now: Long): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(f: FatturaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FatturaEntity>)

    @Query("UPDATE fatture SET stato = 'PAGATA', dataPagamento = :ora WHERE id = :id")
    suspend fun segnaPagata(id: Long, ora: Long)

    @Query("DELETE FROM fatture")
    suspend fun cancellaTutte()
}
