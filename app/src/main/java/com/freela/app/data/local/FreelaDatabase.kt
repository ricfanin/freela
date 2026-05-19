package com.freela.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.freela.app.data.local.converter.EnumConverters
import com.freela.app.data.local.dao.ClienteDao
import com.freela.app.data.local.dao.FatturaDao
import com.freela.app.data.local.dao.FileAllegatoDao
import com.freela.app.data.local.dao.InterazioneDao
import com.freela.app.data.local.dao.PreventivoDao
import com.freela.app.data.local.dao.SessioneLavoroDao
import com.freela.app.data.local.dao.TagDao
import com.freela.app.data.local.dao.TaskDao
import com.freela.app.data.local.entity.ClienteEntity
import com.freela.app.data.local.entity.ClienteTagCrossRef
import com.freela.app.data.local.entity.FatturaEntity
import com.freela.app.data.local.entity.FileAllegatoEntity
import com.freela.app.data.local.entity.InterazioneEntity
import com.freela.app.data.local.entity.PreventivoEntity
import com.freela.app.data.local.entity.SessioneLavoroEntity
import com.freela.app.data.local.entity.TagEntity
import com.freela.app.data.local.entity.TaskEntity

@Database(
    entities = [
        ClienteEntity::class,
        TagEntity::class,
        ClienteTagCrossRef::class,
        InterazioneEntity::class,
        TaskEntity::class,
        SessioneLavoroEntity::class,
        PreventivoEntity::class,
        FatturaEntity::class,
        FileAllegatoEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(EnumConverters::class)
abstract class FreelaDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun tagDao(): TagDao
    abstract fun interazioneDao(): InterazioneDao
    abstract fun taskDao(): TaskDao
    abstract fun sessioneLavoroDao(): SessioneLavoroDao
    abstract fun preventivoDao(): PreventivoDao
    abstract fun fatturaDao(): FatturaDao
    abstract fun fileAllegatoDao(): FileAllegatoDao

    companion object {
        const val NAME = "freela.db"
    }
}
