package com.freela.app.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.freela.app.MainActivity
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

    // uso il taskId come id notifica così posso aggiornarla o cancellarla, e al tap apre la lista task
    fun notificaTaskReminder(taskId: Long, titolo: String, sottotitolo: String?) {
        if (!notificheConsentite()) return

        val contentIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_NAV_DESTINATION, NAV_TASK)
                putExtra(EXTRA_TASK_ID, taskId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TASK_REMINDER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titolo)
            .apply { if (!sottotitolo.isNullOrBlank()) setContentText(sottotitolo) }
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(taskId.toInt(), notification)
        }
    }

    fun buildTimerNotification(
        titolo: String,
        inizioMillis: Long,
        stopIntent: PendingIntent,
        contentIntent: PendingIntent,
    ): android.app.Notification =
        NotificationCompat.Builder(context, CHANNEL_TIMER)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titolo)
            .setContentText(context.getString(R.string.timer_notification_text))
            .setUsesChronometer(true)
            .setWhen(inizioMillis)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(contentIntent)
            .addAction(0, context.getString(R.string.timer_action_stop), stopIntent)
            .build()

    private fun notificheConsentite(): Boolean =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

    companion object {
        const val CHANNEL_TASK_REMINDER = "task_reminder"
        const val CHANNEL_PAYMENT_REMINDER = "payment_reminder"
        const val CHANNEL_TIMER = "timer_attivo"

        const val TIMER_NOTIFICATION_ID = 1001

        const val EXTRA_NAV_DESTINATION = "nav_destination"
        const val EXTRA_TASK_ID = "task_id"
        const val NAV_TASK = "task"
    }
}
