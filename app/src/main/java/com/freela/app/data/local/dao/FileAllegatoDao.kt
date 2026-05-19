package com.freela.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.freela.app.data.local.entity.FileAllegatoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FileAllegatoDao {

    @Query("SELECT * FROM file_allegati WHERE clienteId = :clienteId ORDER BY dataCaricamento DESC")
    fun osservaPerCliente(clienteId: Long): Flow<List<FileAllegatoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: FileAllegatoEntity): Long

    @Query("DELETE FROM file_allegati WHERE id = :id")
    suspend fun delete(id: Long)
}
