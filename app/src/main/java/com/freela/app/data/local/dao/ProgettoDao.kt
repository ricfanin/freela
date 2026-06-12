package com.freela.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.freela.app.data.local.entity.ProgettoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgettoDao {

    @Query("SELECT * FROM progetto ORDER BY dataCreazione DESC")
    fun osservaTutti(): Flow<List<ProgettoEntity>>

    @Query("SELECT * FROM progetto WHERE id = :id")
    fun osserva(id: Long): Flow<ProgettoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progetto: ProgettoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ProgettoEntity>)

    @Update
    suspend fun update(progetto: ProgettoEntity)

    @Query("DELETE FROM progetto WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM progetto")
    suspend fun cancellaTutti()
}
