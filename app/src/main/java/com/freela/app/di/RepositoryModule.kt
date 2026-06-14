package com.freela.app.di

import com.freela.app.data.preferences.SettingsManager
import com.freela.app.data.repository.LocalClienteRepository
import com.freela.app.data.repository.LocalFinanzeRepository
import com.freela.app.data.repository.LocalInterazioneRepository
import com.freela.app.data.repository.LocalProgettoRepository
import com.freela.app.data.repository.LocalTaskRepository
import com.freela.app.data.repository.LocalTimeTrackingRepository
import com.freela.app.data.scheduler.AlarmReminderScheduler
import com.freela.app.domain.repository.ClienteRepository
import com.freela.app.domain.repository.FinanzeRepository
import com.freela.app.domain.repository.InterazioneRepository
import com.freela.app.domain.repository.ProgettoRepository
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TaskRepository
import com.freela.app.domain.repository.TimeTrackingRepository
import com.freela.app.domain.scheduler.ReminderScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// lega le interfacce alle impl locali, in futuro qui si potrà bindare una versione remota
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindClienteRepository(impl: LocalClienteRepository): ClienteRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: LocalTaskRepository): TaskRepository

    @Binds
    @Singleton
    abstract fun bindProgettoRepository(impl: LocalProgettoRepository): ProgettoRepository

    @Binds
    @Singleton
    abstract fun bindInterazioneRepository(impl: LocalInterazioneRepository): InterazioneRepository

    @Binds
    @Singleton
    abstract fun bindTimeTrackingRepository(impl: LocalTimeTrackingRepository): TimeTrackingRepository

    @Binds
    @Singleton
    abstract fun bindFinanzeRepository(impl: LocalFinanzeRepository): FinanzeRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsManager): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindReminderScheduler(impl: AlarmReminderScheduler): ReminderScheduler
}
