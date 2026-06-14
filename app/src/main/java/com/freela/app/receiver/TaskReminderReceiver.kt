package com.freela.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.freela.app.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.freela.app.domain.repository.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// quando scatta l'alarm mostro la notifica del task, ma se nel frattempo è stato
// completato o cancellato non notifico niente
@AndroidEntryPoint
class TaskReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var taskRepository: TaskRepository
    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        if (taskId <= 0L) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val task = taskRepository.osservaTutti().first().firstOrNull { it.id == taskId }
                if (task != null && !task.completato) {
                    notificationHelper.notificaTaskReminder(
                        taskId = task.id,
                        titolo = task.titolo,
                        sottotitolo = task.descrizione,
                    )
                }
            } finally {
                pending.finish()
            }
        }
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
    }
}
