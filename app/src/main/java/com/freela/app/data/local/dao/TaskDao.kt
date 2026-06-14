package com.freela.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.freela.app.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task ORDER BY scadenza ASC")
    fun osservaTutti(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE completato = 0 ORDER BY scadenza ASC")
    fun osservaAperti(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE clienteId = :clienteId AND completato = 0 ORDER BY scadenza ASC LIMIT 1")
    fun osservaProssimoPerCliente(clienteId: Long): Flow<TaskEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Query("UPDATE task SET completato = 1, dataCompletamento = :ora WHERE id = :id")
    suspend fun completa(id: Long, ora: Long)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM task")
    suspend fun cancellaTutti()
}
