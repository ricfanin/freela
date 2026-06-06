package com.freela.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.freela.app.domain.repository.TaskRepository
import com.freela.app.domain.scheduler.ReminderScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Riarma i reminder dopo il riavvio del dispositivo (PRD FR-11, NFR-16).
 *
 * Gli alarm di AlarmManager non sopravvivono al reboot: qui rileggiamo i task
 * aperti con scadenza futura e li rischeduliamo.
 *
 * Su alcuni vendor (Xiaomi, Huawei) l'autostart è limitata: documentato nel PRD §12 rischi.
 */
@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject lateinit var taskRepository: TaskRepository
    @Inject lateinit var scheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val now = System.currentTimeMillis()
                taskRepository.osservaAperti().first()
                    .filter { !it.completato && it.scadenza > now }
                    .forEach { scheduler.schedula(it) }
            } finally {
                pending.finish()
            }
        }
    }
}
