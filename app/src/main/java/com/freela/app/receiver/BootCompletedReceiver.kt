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

// gli alarm non sopravvivono al reboot, quindi qui rileggo i task aperti futuri e li
// rischedulo. nota: su alcuni vendor (xiaomi, huawei) l'autostart è limitata
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
