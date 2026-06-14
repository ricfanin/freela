package com.freela.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.freela.app.data.seed.SeedDataSource
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TaskRepository
import com.freela.app.domain.scheduler.ReminderScheduler
import com.freela.app.notification.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltAndroidApp
class FreelaApp : Application(), Configuration.Provider {

    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var taskRepository: TaskRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler
    @Inject lateinit var seedDataSource: SeedDataSource
    @Inject lateinit var settingsRepository: SettingsRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createChannels()
        riarmaReminderAperti()
        riseminaSeVuoto()
    }

    // se una migrazione distruttiva ha svuotato il db e l'onboarding era già fatto,
    // rimetto i dati demo così l'app non resta vuota
    private fun riseminaSeVuoto() {
        appScope.launch {
            if (settingsRepository.onboardingCompleted.first()) {
                seedDataSource.seedIfEmpty()
            }
        }
    }

    // riarmo gli alarm dei task aperti futuri all'avvio, serve a coprire quelli creati
    // prima che gli alarm fossero impostati (es. dati seed)
    private fun riarmaReminderAperti() {
        appScope.launch {
            val now = System.currentTimeMillis()
            taskRepository.osservaAperti().first()
                .filter { !it.completato && it.scadenza > now }
                .forEach { reminderScheduler.schedula(it) }
        }
    }
}
