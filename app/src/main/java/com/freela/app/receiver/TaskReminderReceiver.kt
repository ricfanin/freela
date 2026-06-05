package com.freela.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Stub receiver per i reminder schedulati via AlarmManager (PRD FR-10).
 *
 * Implementazione completa nella fase 6 del PRD §11.4:
 * - leggere taskId dall'intent extra
 * - costruire una notifica sul canale CHANNEL_TASK_REMINDER
 * - pending intent al dettaglio task
 */
class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // TODO PRD §11.4 fase 6
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
    }
}
