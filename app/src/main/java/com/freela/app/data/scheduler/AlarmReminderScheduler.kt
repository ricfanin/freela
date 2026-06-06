package com.freela.app.data.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.getSystemService
import com.freela.app.domain.model.Task
import com.freela.app.domain.scheduler.ReminderScheduler
import com.freela.app.receiver.TaskReminderReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementazione di [ReminderScheduler] basata su AlarmManager (PRD FR-10).
 *
 * Usa exact alarm (`setExactAndAllowWhileIdle`) così che la notifica scatti
 * puntuale anche in Doze. Il PendingIntent punta a [TaskReminderReceiver];
 * `requestCode = taskId` garantisce un alarm univoco e cancellabile per task.
 */
@Singleton
class AlarmReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReminderScheduler {

    private val alarmManager: AlarmManager? = context.getSystemService()

    override fun schedula(task: Task) {
        val am = alarmManager ?: return
        if (task.completato || task.scadenza <= System.currentTimeMillis()) return

        // Su API 31+ gli exact alarm richiedono il permesso: se non concesso, evitiamo il crash.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
            !am.canScheduleExactAlarms()
        ) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.scadenza, pendingIntent(task.id))
            return
        }
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, task.scadenza, pendingIntent(task.id))
    }

    override fun annulla(taskId: Long) {
        val am = alarmManager ?: return
        am.cancel(pendingIntent(taskId))
    }

    private fun pendingIntent(taskId: Long): PendingIntent {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TASK_ID, taskId)
        }
        return PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
