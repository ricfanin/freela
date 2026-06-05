package com.freela.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import com.freela.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun createChannels() {
        val manager = context.getSystemService<NotificationManager>() ?: return

        val taskReminder = NotificationChannel(
            CHANNEL_TASK_REMINDER,
            context.getString(R.string.channel_task_reminder_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.channel_task_reminder_desc)
        }

        val paymentReminder = NotificationChannel(
            CHANNEL_PAYMENT_REMINDER,
            context.getString(R.string.channel_payment_reminder_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.channel_payment_reminder_desc)
        }

        val timer = NotificationChannel(
            CHANNEL_TIMER,
            context.getString(R.string.channel_timer_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = context.getString(R.string.channel_timer_desc)
            setShowBadge(false)
        }

        manager.createNotificationChannels(listOf(taskReminder, paymentReminder, timer))
    }

    companion object {
        const val CHANNEL_TASK_REMINDER = "task_reminder"
        const val CHANNEL_PAYMENT_REMINDER = "payment_reminder"
        const val CHANNEL_TIMER = "timer_attivo"
    }
}
