package com.freela.app.di

import android.content.Context
import androidx.room.Room
import com.freela.app.data.local.FreelaDatabase
import com.freela.app.data.local.dao.ClienteDao
import com.freela.app.data.local.dao.FatturaDao
import com.freela.app.data.local.dao.FileAllegatoDao
import com.freela.app.data.local.dao.InterazioneDao
import com.freela.app.data.local.dao.PreventivoDao
import com.freela.app.data.local.dao.ProgettoDao
import com.freela.app.data.local.dao.SessioneLavoroDao
import com.freela.app.data.local.dao.TagDao
import com.freela.app.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FreelaDatabase =
        Room.databaseBuilder(context, FreelaDatabase::class.java, FreelaDatabase.NAME)
            .fallbackToDestructiveMigration() // V1 in dev. Per release: definire Migration esplicite (PRD §7.2 NFR-06).
            .build()

    @Provides fun provideClienteDao(db: FreelaDatabase): ClienteDao = db.clienteDao()
    @Provides fun provideTagDao(db: FreelaDatabase): TagDao = db.tagDao()
    @Provides fun provideInterazioneDao(db: FreelaDatabase): InterazioneDao = db.interazioneDao()
    @Provides fun provideTaskDao(db: FreelaDatabase): TaskDao = db.taskDao()
    @Provides fun provideSessioneLavoroDao(db: FreelaDatabase): SessioneLavoroDao = db.sessioneLavoroDao()
    @Provides fun providePreventivoDao(db: FreelaDatabase): PreventivoDao = db.preventivoDao()
    @Provides fun provideFatturaDao(db: FreelaDatabase): FatturaDao = db.fatturaDao()
    @Provides fun provideFileAllegatoDao(db: FreelaDatabase): FileAllegatoDao = db.fileAllegatoDao()
    @Provides fun provideProgettoDao(db: FreelaDatabase): ProgettoDao = db.progettoDao()
}
