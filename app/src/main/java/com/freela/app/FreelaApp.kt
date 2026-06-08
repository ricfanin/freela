package com.freela.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.freela.app.domain.repository.TaskRepository
import com.freela.app.domain.scheduler.ReminderScheduler
import com.freela.app.notification.NotificationHelper
import com.freela.app.worker.SuggerimentiWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
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

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createChannels()
        schedulaSuggerimenti()
        riarmaReminderAperti()
    }

    /** WorkManager periodico per i suggerimenti di follow-up (PRD FR-12). */
    private fun schedulaSuggerimenti() {
        val request = PeriodicWorkRequestBuilder<SuggerimentiWorker>(12, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SuggerimentiWorker.UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    /**
     * Riarma i reminder dei task aperti con scadenza futura all'avvio (FR-10/11).
     * Copre i task creati prima dell'installazione degli alarm (es. dati seed).
     */
    private fun riarmaReminderAperti() {
        appScope.launch {
            val now = System.currentTimeMillis()
            taskRepository.osservaAperti().first()
                .filter { !it.completato && it.scadenza > now }
                .forEach { reminderScheduler.schedula(it) }
        }
    }
}
