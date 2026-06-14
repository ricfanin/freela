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

// uso exact alarm così la notifica scatta puntuale anche in doze.
// il requestCode = taskId mi dà un alarm univoco e cancellabile per ogni task
@Singleton
class AlarmReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReminderScheduler {

    private val alarmManager: AlarmManager? = context.getSystemService()

    override fun schedula(task: Task) {
        val am = alarmManager ?: return
        if (task.completato || task.scadenza <= System.currentTimeMillis()) return

        // su api 31+ gli exact alarm vogliono il permesso, se manca ripiego sull'inexact per non crashare
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
